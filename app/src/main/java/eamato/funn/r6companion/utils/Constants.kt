package eamato.funn.r6companion.utils

import eamato.funn.r6companion.R

const val SIMPLE_OPERATOR_IMAGE_WIDTH = 100
const val SIMPLE_OPERATOR_IMAGE_HEIGHT = 100

const val ROULETTE_OPERATOR_IMAGE_WIDTH = 300
const val ROULETTE_OPERATOR_IMAGE_HEIGHT = 300

const val OPERATOR_IMAGE_WIDTH = 400
const val OPERATOR_IMAGE_HEIGHT = 400

const val WINNER_OPERATOR_IMAGE_WIDTH = 600
const val WINNER_OPERATOR_IMAGE_HEIGHT = 600

const val ROULETTE_OPERATOR_ICON_WIDTH = 75
const val ROULETTE_OPERATOR_ICON_HEIGHT = 75

const val OUR_TEAM_IMAGE_WIDTH = 75
const val OUR_TEAM_IMAGE_HEIGHT = 75

const val UN_EXISTENT_HOST = "https://unexisting.host.mne/"

const val ROULETTE_SEARCH_QUERY_KEY = "searchViewQuery"

const val NEWS_HOST = " https://nimbus.ubisoft.com/"
const val R6STATS_HOST = "https://r6stats.com/"
const val NEWS_PATH = "api/v1/items"
const val NEWS_TAG_PARAM_KEY = "tags"
const val NEWS_COUNT_PARAM_KEY = "limit"
const val NEWS_SKIP_PARAM_KEY = "skip"
const val NEWS_CATEGORIES_FILTER_PARAM_KEY = "categoriesFilter"
const val NEWS_LOCALE_PARAM_KEY = "locale"
const val NEWS_TAG_PARAM_R6_VALUE = "BR-rainbow-six GA-siege"

const val NEWS_CATEGORIES_FILTER_PARAM_NEWS_VALUE = "news"
const val NEWS_CATEGORIES_FILTER_PARAM_ESPORTS_VALUE = "esports"
const val NEWS_CATEGORIES_FILTER_PARAM_GAME_UPDATES_VALUE = "game-updates"
const val NEWS_CATEGORIES_FILTER_PARAM_COMMUNITY_VALUE = "community"
const val NEWS_CATEGORIES_FILTER_PARAM_PATCH_NOTES_VALUE = "patch-notes"
const val NEWS_CATEGORIES_FILTER_PARAM_STORE_VALUE = "store"

const val OPERATORS_PATH = "api/database/operators"
const val NEWS_COUNT_MAX_VALUE = 100
const val NEWS_COUNT_DEFAULT_VALUE = 60
const val NEWS_PREFETCH_DISTANCE = NEWS_COUNT_DEFAULT_VALUE / 4
const val NEWS_MAX_PAGE_SIZE = 3 * NEWS_COUNT_DEFAULT_VALUE
const val AD_INSERTION_COUNT = 15

const val CONNECTION_TIME_OUT = 5 * 1000L
const val READ_TIME_OUT = 5 * 1000L
const val WRITE_TIME_OUT = 5 * 1000L

const val DEFAULT_NOTIFICATION_CHANNEL_ID = "default_notification_channel_id"

const val PREFERENCE_SAVE_SELECTIONS_KEY = "save_selections"
const val PREFERENCE_DARK_MODE_KEY = "dark_mode"
const val PREFERENCE_DARK_MODE_VALUE_OFF = "dm_off"
const val PREFERENCE_DARK_MODE_VALUE_ON = "dm_on"
const val PREFERENCE_DARK_MODE_VALUE_ADAPTIVE = "dm_adaptive"
const val PREFERENCE_DARK_MODE_VALUE_SYSTEM_DEFAULT = "dm_system_default"
const val PREFERENCE_DARK_MODE_VALUE_SET_BY_BATTERY_SAVER = "dm_battery_saver"
const val PREFERENCE_DARK_MODE_DEFAULT_VALUE = PREFERENCE_DARK_MODE_VALUE_OFF
const val PREFERENCE_ILLUMINATION_THRESHOLD_KEY = "illumination_threshold"
const val PREFERENCE_USE_MOBILE_NETWORK_FOR_IMAGE_DOWNLOAD_KEY = "use_mobile_network_for_image_downloading"
const val PREFERENCE_USE_MOBILE_NETWORK_FOR_IMAGE_DOWNLOAD_DEFAULT_VALUE = false
const val PREFERENCE_FAVOURITE_UPDATES_KEY = "favourite_updates"
const val PREFERENCE_ABOUT_KEY = "about"

const val DARK_MODE_ADAPTIVE_THRESHOLD = 1000

const val DARK_MODE_SWITCHER_DELAY = 2 * 1000L

const val RUSSIAN_LANGUAGE_CODE = "ru"
const val ENGLISH_LANGUAGE_CODE = "en"

const val ENGLISH_NEWS_LOCALE = "en-us"
const val RUSSIAN_NEWS_LOCALE = "ru-ru"
const val DEFAULT_NEWS_LOCALE = ENGLISH_NEWS_LOCALE

const val OPERATORS_CACHE_FILE_NAME = "operators.txt"

const val NEWS_AUTHORIZATION_TOKEN_HEADER = "authorization: 3u0FfSBUaTSew-2NVfAOSYWevVQHWtY9q3VM8Xx9Lto"

const val IMAGE_LOGGER_TAG = "ImageRequest"

val NEWS_CATEGORIES = listOf(
    R.string.news_category_all to null,
    R.string.news_category_esport to NEWS_CATEGORIES_FILTER_PARAM_ESPORTS_VALUE,
    R.string.news_category_game_updates to NEWS_CATEGORIES_FILTER_PARAM_GAME_UPDATES_VALUE,
    R.string.news_category_community to NEWS_CATEGORIES_FILTER_PARAM_COMMUNITY_VALUE,
    R.string.news_category_patch_notes to NEWS_CATEGORIES_FILTER_PARAM_PATCH_NOTES_VALUE,
    R.string.news_category_store to NEWS_CATEGORIES_FILTER_PARAM_STORE_VALUE
)