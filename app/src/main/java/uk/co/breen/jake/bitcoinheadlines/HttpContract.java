package uk.co.breen.jake.bitcoinheadlines;

/**
 * Created by Jake on 13/01/2017.
 */

public class HttpContract {

    //public static final String URL_GET_SOURCE_LIST ="http://www.jakebreen.co.uk/bitcoinheadlines/sites/source_list.php";
    public static final String URL_GET_SOURCE_LIST ="http://www.jakebreen.co.uk/bitcoinheadlines/sites/jsonString.txt";
    public static final String URL_GET_RSS_HEADLINES ="http://www.jakebreen.co.uk/bitcoinheadlines/sites/headline_parser.php";
    public static final String URL_GET_FEEDBACK_URL ="http://www.jakebreen.co.uk/bitcoinheadlines/insertFeedback.php";
    //public static final String URL_GET_BITCOIN_PRICE = "http://api.coindesk.com/v1/bpi/currentprice.json";
    public static final String URL_GET_BITCOIN_PRICE = "https://blockchain.info/ticker";

    public static final String TAG_SOURCE_ID = "id";
    public static final String TAG_SOURCE_TITLE = "title";
    public static final String TAG_SOURCE_DESCRIPTION = "description";
    public static final String TAG_SOURCE_DATE_ARRAY = "dateTimeList";
    public static final String TAG_SOURCE_IMAGE = "image";
    public static final String TAG_SOURCE_RSS_URL = "rss";

    public static final String TAG_HEADLINE_TITLE = "title";
    public static final String TAG_HEADLINE_DATETIME = "dateTime";
    public static final String TAG_HEADLINE_DESCRIPTION = "description";
    public static final String TAG_HEADLINE_LINK = "link";
    public static final String TAG_HEADLINE_AUTHOR = "author";
    public static final String TAG_HEADLINE_CATEGORIES = "categories";
    public static final String TAG_HEADLINE_IMAGE = "image";
    public static final String TAG_HEADLINE_FULLDATETIME = "fullDateTime";

    public static final String TAG_BPI = "bpi";
    public static final String TAG_USD = "USD";
    public static final String TAG_GBP = "GBP";
    public static final String TAG_EUR = "EUR";
    public static final String TAG_LAST_RATE = "last";
}
