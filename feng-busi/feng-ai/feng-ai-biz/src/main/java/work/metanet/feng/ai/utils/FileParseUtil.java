package work.metanet.feng.ai.utils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;
import org.apache.commons.io.IOUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;
import org.mozilla.universalchardet.UnicodeBOMInputStream;
import org.mozilla.universalchardet.UniversalDetector;

import cn.hutool.core.util.StrUtil;
import lombok.extern.slf4j.Slf4j;
import java.util.regex.*;

/**
 * 文件解析工具类
 * @author edison / hunting-info
 * @copyright Copyright (C) 2023 <a href="https://hunting-info.ltd">狩猎信息</a>
 * @license SPDX-License-Identifier: AGPL-3.0-or-later
 */

@Slf4j
public class FileParseUtil {

	public static String extractFileContent(String url, String ext) {
		if (StrUtil.isBlank(url))
			return "";

		String content = "";
		try {
			if (StrUtil.equalsIgnoreCase(ext, "PDF")) {
				// 使用PDFBox提取PDF内容
				try (PDDocument document = PDDocument.load(new URL(url).openStream())) {
					PDFTextStripper stripper = new PDFTextStripper();
					content = stripper.getText(document);
				}
			} else if (StrUtil.equalsAnyIgnoreCase(ext, "DOC", "DOCX")) {
				// 使用Apache POI提取Word文档内容
				if (StrUtil.equalsIgnoreCase(ext, "DOC")) {
					// 处理.doc文件
					HWPFDocument doc = new HWPFDocument(new URL(url).openStream());
					content = doc.getDocumentText();
				} else {
					// 处理.docx文件
					XWPFDocument docx = new XWPFDocument(new URL(url).openStream());
					for (XWPFParagraph p : docx.getParagraphs()) {
						content += p.getText() + "\n";
					}
					for (XWPFTable tbl : docx.getTables()) {
						for (XWPFTableRow row : tbl.getRows()) {
							for (XWPFTableCell cell : row.getTableCells()) {
								for (XWPFParagraph p : cell.getParagraphs()) {
									content += p.getText() + "\t";
								}
							}
							content += "\n";
						}
					}
				}
			} else if (StrUtil.equalsAnyIgnoreCase(ext, "TXT", "TEXT", "md", "html", "htm", "json", "xml")) {
				// 处理文本文件,解决乱码问题
				// content = IOUtils.toString(new URL(url).openStream(),
				// StandardCharsets.UTF_8);
				content = readFileWithDetectedEncoding(url);

			} else {
				log.warn("不支持的文件类型: {}", ext);
				return "";
			}

			// 清理内容中的多余空格和换行
			content = content.replaceAll("\\s+", " ").trim();

			return content;
		} catch (Exception e) {
			log.error("提取文件内容失败 - ext: {}, url: {}", ext, url, e);
			return "";
		}
	}

	public static String getFileExtension(String fileName) {
		if (fileName == null) {
			return "";
		}

		Pattern pattern = Pattern.compile("(?<=.)\\.[^.]+$");
		Matcher matcher = pattern.matcher(fileName);

		if (matcher.find()) {
			return matcher.group().substring(1).toLowerCase();
		}
		return "";
	}

	private static String detectFileEncoding(File file) throws IOException {
		UniversalDetector detector = new UniversalDetector(null);

		try (InputStream inputStream = new FileInputStream(file)) {
			byte[] buf = new byte[4096];
			int nread;
			while ((nread = inputStream.read(buf)) > 0 && !detector.isDone()) {
				detector.handleData(buf, 0, nread);
			}
		}
		detector.dataEnd();

		String encoding = detector.getDetectedCharset();
		detector.reset();

		// 如果检测失败，按优先级使用备选编码
		if (encoding == null) {
			return Charset.defaultCharset().name(); // 系统默认编码
		}
		return encoding;
	}

	public static String readFileWithDetectedEncoding(File file) throws IOException {
		// 第一步：检测文件编码
		String encoding = detectFileEncoding(file);

		// 第二步：处理BOM头（Byte Order Mark）
		try (InputStream inputStream = new UnicodeBOMInputStream(new FileInputStream(file))) {
			((UnicodeBOMInputStream) inputStream).skipBOM();

			// 第三步：使用检测到的编码读取内容
			return IOUtils.toString(inputStream, encoding);
		}
	}

	public static String readFileWithDetectedEncoding(String url) throws IOException {
		// 创建输入流（自动区分本地文件/网络资源）
		try (InputStream rawStream = getInputStream(url)) {
			// 包装为支持mark/reset的流
			BufferedInputStream bufferedStream = new BufferedInputStream(rawStream);
			bufferedStream.mark(Integer.MAX_VALUE); // 标记流的起始位置

			// 检测编码（复用输入流）
			String encoding = detectEncoding(bufferedStream);
			bufferedStream.reset(); // 重置流到标记位置

			// 处理BOM并读取内容
			try (UnicodeBOMInputStream bomStream = new UnicodeBOMInputStream(bufferedStream)) {
				bomStream.skipBOM();
				return IOUtils.toString(bomStream, encoding);
			}
		}
	}

	// 统一获取输入流（支持本地和网络）
	private static InputStream getInputStream(String url) throws IOException {
		if (url.startsWith("http://") || url.startsWith("https://")) {
			URLConnection conn = new URL(url).openConnection();
			conn.setConnectTimeout(5000);
			conn.setReadTimeout(10000);
			return conn.getInputStream();
		} else {
			return new FileInputStream(url);
		}
	}

	// 从输入流检测编码（优化版）
	private static String detectEncoding(InputStream is) throws IOException {
		UniversalDetector detector = new UniversalDetector(null);
		byte[] buf = new byte[4096];
		int nread;
		while ((nread = is.read(buf)) > 0 && !detector.isDone()) {
			detector.handleData(buf, 0, nread);
		}
		detector.dataEnd();

		String encoding = detector.getDetectedCharset();
		detector.reset();

		// 中文编码优化处理
		if (encoding == null) {
			return Charset.defaultCharset().name();
		} else if ("GB18030".equalsIgnoreCase(encoding)) {
			return "GBK"; // 优先使用GBK
		}
		return encoding;
	}

	// 大文件处理：通过分块处理避免内存溢出
	public static String readLargeFile(String url) throws IOException {
		try (InputStream is = getInputStream(url);
				BufferedReader reader = new BufferedReader(new InputStreamReader(is, detectEncoding(is)))) {

			StringBuilder sb = new StringBuilder();
			char[] buffer = new char[8192];
			int charsRead;
			while ((charsRead = reader.read(buffer)) != -1) {
				sb.append(buffer, 0, charsRead);
			}
			return sb.toString();
		}
	}
}
