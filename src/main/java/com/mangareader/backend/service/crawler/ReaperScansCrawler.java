package com.mangareader.backend.service.crawler;

import com.mangareader.backend.dto.SearchResultDto;
import com.mangareader.backend.entity.Manga;
import lombok.NoArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.jetbrains.annotations.NotNull;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;
import org.springframework.util.StopWatch;
import org.springframework.util.concurrent.ListenableFuture;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

import static com.mangareader.backend.data.service.Utils.getDownloadPath;
import static com.mangareader.backend.data.service.Utils.getRelativePath;

/**
 * Crawler implementation for ReaperScans website.
 */
@Log4j2
@Service
@NoArgsConstructor
public class ReaperScansCrawler extends Crawler {

	private static final String CHAPTER = "chapter-";

	@Override
	protected String getBaseUrl() {
		return "https://reaperscans.com";
	}

	@Override
	public List<String> parseChapter(@NotNull Manga entity, @NotNull Integer chapterID) {
		Document document = getDocument(toUrl(getBaseUrl(), "/series/", entity.getUrlName(), "/", CHAPTER, chapterID.toString()));

		if (document != null) {
			log.info("Parsing images from ReaperScans.");
			return parseImages(document);
		}

		return attemptGoogleSearch(entity, chapterID);
	}

	private List<String> attemptGoogleSearch(Manga entity, Integer targetChapterID) {
		String chapterUrl = getChapterUrlGoogle(entity, targetChapterID);

		if (chapterUrl != null && chapterUrl.endsWith(CHAPTER + targetChapterID)) {
			Document chapterDocument = getDocumentWithNumberOfRetries(chapterUrl, 3);
			if (chapterDocument != null) {
				log.info("Parsing images from Google Search.");
				return parseImages(chapterDocument);
			}
		}

		return attemptAdjacentChaptersGoogleSearch(entity, targetChapterID);
	}

	private List<String> attemptAdjacentChaptersGoogleSearch(Manga entity, Integer targetChapterID) {
		List<Integer> chapterIdsToCheck = List.of(targetChapterID + 1, targetChapterID - 1);

		for (Integer adjacentChapterId : chapterIdsToCheck) {
			String chapterUrl = getChapterUrlGoogle(entity, adjacentChapterId);

			if (chapterUrl == null || !chapterUrl.endsWith(CHAPTER + (adjacentChapterId))) {
				continue;
			}

			chapterUrl = getLinkFromAdjacentChapter(chapterUrl, targetChapterID);
			if (chapterUrl != null) {
				Document chapterDocument = getDocumentWithNumberOfRetries(getBaseUrl() + chapterUrl, 3);
				if (chapterDocument != null) {
					log.info("Parsing images from ADJACENT chapter {} Google Search.", adjacentChapterId);
					return parseImages(chapterDocument);
				}
			}
		}

		return Collections.emptyList();
	}

	private String getLinkFromAdjacentChapter(String chapterUrl, Integer chapterID) {
		Document chapterDocument = getDocument(chapterUrl);
		return chapterDocument.select("nav > div > a")
			.stream()
			.map(anchorTag -> anchorTag.attr("href"))
			.filter(url -> url.endsWith(CHAPTER + chapterID))
			.findFirst()
			.orElse(null);
	}

	private String getChapterUrlGoogle(@NotNull Manga entity, Integer chapterID) {
		String googleSearchUrl = getGoogleSearchUrlForChapter(getBaseUrl(), entity.getName(), chapterID);
		Document document = getDocument(googleSearchUrl);
		return parseChapterURL(document);
	}

	@Override
	protected Integer parseLatestChapterNumber(@NotNull Document document) {
		return document.select("div > div > a[href]")
			.stream()
			.map(Element::attributes)
			.map(atr -> atr.get("href"))
			.filter(href -> href.startsWith("/series"))
			.map(href -> href.split("chapter-")[1])
			.map(Integer::parseInt)
			.max(Integer::compare)
			.orElse(null);
	}

	private String parseChapterURL(@NotNull Document document, String chapterNumber) {
		return document.select("a[href]")
			.stream()
			.map(e -> e.attr("href"))
			.filter(e -> e.endsWith(CHAPTER + chapterNumber))
			.findFirst()
			.orElse(null);
	}

	@Override
	protected String parseChapterURL(@NotNull Document document) {
		return document.select("a[href] > div > div > h3")
			.parents()
			.stream()
			.filter(item -> item.tagName().equals("a"))
			.map(item -> item.absUrl("href"))
			// Google returns URLs in format "http://www.google.com/url?q=<url>&sa=U&ei=<someKey>"
			.map(item -> URLDecoder.decode(item.substring(item.indexOf('=') + 1, item.indexOf('&')), StandardCharsets.UTF_8))
			.findAny()
			.orElse(null);
	}

	@Override
	protected List<String> parseImages(@NotNull Document document) {
		List<String> imageSources = document.select("img[src]")
			.stream()
			.map(e -> e.attr("src"))
			.filter(e -> e.contains("media"))
			.toList();

		List<String> result = new ArrayList<>();
		result.add(strip(imageSources.get(1)));

		imageSources.subList(2, imageSources.size())
			.forEach(source -> {
				if (!source.matches(".*?\\d+\\..*?")) {
					return; // early exit
				}

				result.add(source.endsWith(".jpg") || source.endsWith(".png")
					? source
					: strip(source));
			});

		return result;
	}

	private String strip(String source) {
		return source
			.split("url=")[1]
			.split("&")[0]
			.replace("%2F", "/")
			.replace("%3A", ":");
	}

	/**
	 * Icon is not downloaded, download it in background, show icon from website
	 *
	 * @param entity the manga entity
	 * @return the icon url
	 */
	@Async
	@Override
	public ListenableFuture<String> asyncLoadIcon(@NotNull Manga entity) {
		Document document = getDocument(toUrl(getBaseUrl(), entity.getUrlName()));
		entity.setLatestChNum(parseLatestChapterNumber(document));
		String iconUrl = document.select("div > img[src]")
			.stream()
			.map(e -> e.attr("src"))
			.findFirst()
			.orElse(null);

		new Thread(() -> asyncDownloadIcon(entity, iconUrl)).start();

		assert iconUrl != null;
		return AsyncResult.forValue(iconUrl);
	}

	//TODO: is this not also a super method?
	@Async
	private void asyncDownloadIcon(@NotNull Manga entity, String iconUrl) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();
		log.info("Started download for %s".formatted(entity.getName()));

		String newIconPath = getDownloadPath(entity.getUrlName());
		writeImage(newIconPath, getImage(iconUrl));
		entity.setIconPath(getRelativePath(newIconPath));

		stopWatch.stop();
		log.info("Finished download for %s icon in %d ms.".formatted(entity.getName(), stopWatch.getLastTaskTimeMillis()));
	}

	@Async
	public ListenableFuture<Integer> fetchLatestChapterNumber(@NotNull Manga entity) {
		StopWatch stopWatch = new StopWatch();
		stopWatch.start();

		Document document = getDocument(toUrl(getBaseUrl(), "/series/", entity.getUrlName()));

		stopWatch.stop();
		log.info("Latest chapter update for %s in %d ms".formatted(entity.getName(), stopWatch.getLastTaskTimeMillis()));

		assert document != null;
		return AsyncResult.forValue(parseLatestChapterNumber(document));
	}

	/**
	 * Creates an HTTP connection with set requested properties to impersonate real request.
	 * Parse response and create SearchResultDtos with urls, names, the latest chapters and icons of mangas.
	 * <p>
	 * Resource: <a href="https://curlconverter.com/java/">CURL converter for Java</a>
	 *
	 * @param value the searched query string
	 * @return list of search results dtos, containing urls, names, the latest chapters and icons of mangas
	 */
	//TODO: move to super class and rename method
	public List<SearchResultDto> getMangaUrl(String value) {
		if (value == null) {
			return Collections.emptyList();
		}

		try {
			StopWatch stopWatch = new StopWatch();
			stopWatch.start();

			HttpURLConnection httpConn = getHttpURLConnection();

			httpConn.setDoOutput(true);
			OutputStreamWriter writer = new OutputStreamWriter(httpConn.getOutputStream());
			writer.write("{\"fingerprint\":{\"id\":\"olJuYSFBkawm5K7qqSJk\",\"name\":\"frontend.dtddzhx-ghvjlgrpt\",\"locale\":\"en\",\"path\":\"/\",\"method\":\"GET\",\"v\":\"acj\"},\"serverMemo\":{\"children\":[],\"errors\":[],\"htmlHash\":\"5a182466\",\"data\":{\"query\":\"\",\"comics\":[],\"novels\":[]},\"dataMeta\":[],\"checksum\":\"ecf28746c7fd2589eebf011c40e8c26656c8e73c52c47714f79d35e433a3b834\"},\"updates\":[{\"type\":\"syncInput\",\"payload\":{\"id\":\"enwxj\",\"name\":\"query\"," +
				"\"value\":\"%s\"}}]}".formatted(value));
			writer.flush();
			writer.close();
			httpConn.getOutputStream().close();

			InputStream responseStream = httpConn.getResponseCode() / 100 == 2
				? httpConn.getInputStream()
				: httpConn.getErrorStream();
			Scanner s = new Scanner(responseStream).useDelimiter("\\A");
			String response = s.hasNext() ? s.next() : "";

			List<SearchResultDto> resultDtos = parseSearchResultDtos(response);

			stopWatch.stop();
			log.info("Found %d search results for \"%s\" loaded in %d ms"
				.formatted(resultDtos.size(), value, stopWatch.getLastTaskTimeMillis()));

			return resultDtos;
		} catch (IOException e) {
			log.error(e);
			return Collections.emptyList();
		}
	}

	private HttpURLConnection getHttpURLConnection() throws IOException {
		HttpURLConnection httpConn = (HttpURLConnection) new URL("https://reaperscans.com/livewire/message/frontend.dtddzhx-ghvjlgrpt").openConnection();
		httpConn.setRequestMethod("POST");
		httpConn.setRequestProperty("content-type", "application/json");
		httpConn.setRequestProperty("referer", "https://reaperscans.com/");
		httpConn.setRequestProperty("user-agent", "Chrome");
		httpConn.setRequestProperty("x-livewire", "true");
		return httpConn;
	}

	private List<SearchResultDto> parseSearchResultDtos(String response) {
		Document document = Jsoup.parse(response, "UTF-8");
		List<String> urls = parseResponseLinks(document);
		List<String> names = parseResponseNames(document);
		List<String> icons = parsResponseIcons(document);
		List<Integer> latestChapters = parseResponseLatestChapters(document);

		List<SearchResultDto> resultDtos = new ArrayList<>();
		for (int i = 0; i < urls.size(); i++) {
			resultDtos.add(new SearchResultDto(names.get(i), urls.get(i), icons.get(i), latestChapters.get(i)));
		}

		return resultDtos;
	}

	private List<String> parseResponseLinks(@NotNull Document document) {
		return document
			.select("a[href*=comics]")
			.stream()
			.map(e -> e.attr("href"))
			.distinct()
			.map(e -> e.split("/")[4].replace("\\\"", ""))
			.toList();
	}

	private List<String> parseResponseNames(@NotNull Document document) {
		return document
			.select("a[href*=comics] > div > div > p[text-neutral-200]")
			.stream()
			.map(Element::text)
			.map(e -> e.trim().split("\\\\n ")[1])
			.toList();
	}

	private List<String> parsResponseIcons(@NotNull Document document) {
		return document
			.select("a[href*=comics] > div > img[src]")
			.stream()
			.map(e -> e.attr("src"))
			.map(e -> e.replace("\\\"", "").replace("/", ""))
			.toList();
	}

	private List<Integer> parseResponseLatestChapters(@NotNull Document document) {
		return document
			.select("a[href*=comics] > div > div > p > span > span > i > span > i")
			.stream()
			.map(Element::text)
			.map(e -> e.split(" ")[1])
			.map(Integer::parseInt)
			.toList();
	}
}
