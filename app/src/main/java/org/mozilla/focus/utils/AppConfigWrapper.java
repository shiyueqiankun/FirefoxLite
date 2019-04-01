/* -*- Mode: Java; c-basic-offset: 4; tab-width: 4; indent-tabs-mode: nil; -*-
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/. */

package org.mozilla.focus.utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AppConfigWrapper {
    static final int SURVEY_NOTIFICATION_POST_THRESHOLD = 3;
    static final boolean PRIVATE_MODE_ENABLED_DEFAULT = true;
    static final boolean LIFE_FEED_ENABLED_DEFAULT = false;
    static final String LIFE_FEED_PROVIDERS_DEFAULT = "";

    /* Disabled since v1.0.4, keep related code in case we want to enable it again in the future */
    private static final boolean SURVEY_NOTIFICATION_ENABLED = false;
    static final int DRIVE_DEFAULT_BROWSER_FROM_MENU_SETTING_THRESHOLD = 2;

    public static long getRateAppNotificationLaunchTimeThreshold() {
        return FirebaseHelper.getFirebase().getRcLong(FirebaseHelper.RATE_APP_NOTIFICATION_THRESHOLD);
    }

    public static long getShareDialogLaunchTimeThreshold(final boolean needExtend) {
        if (needExtend) {
            return FirebaseHelper.getFirebase().getRcLong(FirebaseHelper.SHARE_APP_DIALOG_THRESHOLD) +
                    getRateAppNotificationLaunchTimeThreshold() -
                    getRateDialogLaunchTimeThreshold();
        }
        return FirebaseHelper.getFirebase().getRcLong(FirebaseHelper.SHARE_APP_DIALOG_THRESHOLD);
    }

    public static long getRateDialogLaunchTimeThreshold() {
        return FirebaseHelper.getFirebase().getRcLong(FirebaseHelper.RATE_APP_DIALOG_THRESHOLD);
    }

    public static int getSurveyNotificationLaunchTimeThreshold() {
        return SURVEY_NOTIFICATION_POST_THRESHOLD;
    }

    public static int getDriveDefaultBrowserFromMenuSettingThreshold() {
        return DRIVE_DEFAULT_BROWSER_FROM_MENU_SETTING_THRESHOLD;
    }

    public static boolean isPrivateModeEnabled() {
        return FirebaseHelper.getFirebase().getRcBoolean(FirebaseHelper.ENABLE_PRIVATE_MODE);
    }

    public static boolean getMyshotUnreadEnabled() {
        return FirebaseHelper.getFirebase().getRcBoolean(FirebaseHelper.ENABLE_MY_SHOT_UNREAD);
    }

    public static boolean isSurveyNotificationEnabled() {
        return SURVEY_NOTIFICATION_ENABLED;
    }

    public static String getRateAppDialogTitle() {
        return FirebaseHelper.getFirebase().getRcString(FirebaseHelper.RATE_APP_DIALOG_TEXT_TITLE);
    }

    public static String getRateAppDialogContent() {
        return FirebaseHelper.getFirebase().getRcString(FirebaseHelper.RATE_APP_DIALOG_TEXT_CONTENT);
    }

    public static String getRateAppPositiveString() {
        return FirebaseHelper.getFirebase().getRcString(FirebaseHelper.RATE_APP_DIALOG_TEXT_POSITIVE);
    }

    public static String getRateAppNegativeString() {
        return FirebaseHelper.getFirebase().getRcString(FirebaseHelper.RATE_APP_DIALOG_TEXT_NEGATIVE);
    }

    public static String getBannerRootConfig() {
        return FirebaseHelper.getFirebase().getRcString(FirebaseHelper.BANNER_MANIFEST);
    }

    public static long getFeatureSurvey() {
        return FirebaseHelper.getFirebase().getRcLong(FirebaseHelper.FEATURE_SURVEY);
    }

    public static String getScreenshotCategoryUrl() {
        return FirebaseHelper.getFirebase().getRcString(FirebaseHelper.SCREENSHOT_CATEGORY_MANIFEST);
    }

    public static String getVpnRecommenderUrl() {
        return FirebaseHelper.getFirebase().getRcString(FirebaseHelper.VPN_RECOMMENDER_URL);
    }

    public static String getVpnRecommenderPackage() {
        return FirebaseHelper.getFirebase().getRcString(FirebaseHelper.VPN_RECOMMENDER_PACKAGE);
    }

    public static long getFirstLaunchWorkerTimer() {
        return FirebaseHelper.getFirebase().getRcLong(FirebaseHelper.FIRST_LAUNCH_TIMER_MINUTES);
    }

    public static String getFirstLaunchNotificationMessage() {
        return FirebaseHelper.getFirebase().getRcString(FirebaseHelper.FIRST_LAUNCH_NOTIFICATION_MESSAGE);
    }

    public static boolean isLifeFeedEnabled() {
        return FirebaseHelper.getFirebase().getRcBoolean(FirebaseHelper.ENABLE_LIFE_FEED);
    }

    public static String getLifeFeedProviderUrl(String provider) {
        String source = FirebaseHelper.getFirebase().getRcString(FirebaseHelper.LIFE_FEED_PROVIDERS);
        String url = "";

        try {
            JSONArray rows = new JSONArray(source);
            for (int i = 0; i < rows.length(); i++) {
                JSONObject row = rows.getJSONObject(i);
                if (row.getString("name").equalsIgnoreCase(provider)) {
                    url = row.getString("url");
                    break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return url;
    }

    static String getShareAppDialogTitle() {
        return FirebaseHelper.getFirebase().getRcString(FirebaseHelper.STR_SHARE_APP_DIALOG_TITLE);
    }

    // Only this field supports prettify
    static String getShareAppDialogContent() {
        final String rcString = FirebaseHelper.getFirebase().getRcString(FirebaseHelper.STR_SHARE_APP_DIALOG_CONTENT);
        return FirebaseHelper.prettify(rcString);
    }

    static String getShareAppMessage() {
        return FirebaseHelper.getFirebase().getRcString(FirebaseHelper.STR_SHARE_APP_DIALOG_MSG);
    }
}
