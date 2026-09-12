package app.morphe.extension.youtube.patches;

import app.morphe.extension.youtube.settings.Settings;

@SuppressWarnings("unused")
public class CaptionCookiesPatch {
    private static final boolean SET_CAPTION_COOKIES = Settings.SET_CAPTION_COOKIES.get();
    private static final String CAPTION_COOKIES = Settings.CAPTION_COOKIES.get();
    private static final String USER_AGENT =
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/140.0.0.0 Safari/537.36";

    private static volatile boolean isTimedTextRequest = false;

    /**
     * 攔截並修改字幕請求網址：自動注入繁體中文翻譯參數 (&tlang=zh-Hant)
     */
    public static String modifyTimedTextUrl(String url) {
        if (url == null || !url.contains("api/timedtext")) {
            isTimedTextRequest = false;
            return url;
        }

        isTimedTextRequest = true;

        // 如果來源本身就是繁體中文，不重複翻譯
        if (url.contains("lang=zh-Hant") || url.contains("lang=zh-TW") || url.contains("tlang=zh-Hant")) {
            return url;
        }

        // 如果網址已經帶有其他翻譯語言，替換為繁體中文
        if (url.contains("&tlang=")) {
            return url.replaceAll("&tlang=[^&]+", "&tlang=zh-Hant");
        }

        // 強制加上繁體中文自動翻譯參數
        return url + "&tlang=zh-Hant";
    }

    public static boolean getRequireCookies() {
        return isTimedTextRequest;
    }

    public static boolean hasCookies() {
        return SET_CAPTION_COOKIES && !CAPTION_COOKIES.isEmpty();
    }

    public static void setRequireCookies(String url) {
        modifyTimedTextUrl(url);
    }

    public static String getCookies() {
        return CAPTION_COOKIES;
    }

    public static String getUserAgent() {
        return USER_AGENT;
    }
}
