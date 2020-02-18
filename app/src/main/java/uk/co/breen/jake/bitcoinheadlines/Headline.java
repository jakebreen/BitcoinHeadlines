package uk.co.breen.jake.bitcoinheadlines;

/**
 * Created by Jake on 04/03/2017.
 */

public class Headline implements Comparable<Headline> {

    private String sourceId, sourceTitle, title, dateTime, sortDate, description, link, author, categories, image, fullDateTime;

    public Headline() {

    }

    public Headline(String titleIn, String dateTimeIn, String descriptionIn, String linkIn, String authorIn, String categoriesIn, String imageIn, String fullDateTimeIn) {
        title = titleIn;
        dateTime = dateTimeIn;
        description = descriptionIn;
        link = linkIn;
        author = authorIn;
        categories = categoriesIn;
        image = imageIn;
        fullDateTime = fullDateTimeIn;
    }

    public Headline(String idIn, String sourceTitleIn, String titleIn, String dateTimeIn, String sortDateIn, String descriptionIn, String linkIn, String authorIn, String categoriesIn, String imageIn, String fullDateTimeIn) {
        sourceId = idIn;
        sourceTitle = sourceTitleIn;
        title = titleIn;
        dateTime = dateTimeIn;
        sortDate = sortDateIn;
        description = descriptionIn;
        link = linkIn;
        author = authorIn;
        categories = categoriesIn;
        image = imageIn;
        fullDateTime = fullDateTimeIn;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return this.getTitle() + ": " + this.getAuthor() ;
    }

    public String getFullDateTime() {
        return fullDateTime;
    }

    public void setFullDateTime(String fullDateTime) {
        this.fullDateTime = fullDateTime;
    }

    public String getSourceId() {
        return sourceId;
    }

    public void setSourceId(String sourceId) {
        this.sourceId = sourceId;
    }

    @Override
    public int compareTo(Headline headline) {
        return getDateTime().compareTo(headline.getDateTime());
    }

    public String getSortDate() {
        return sortDate;
    }

    public void setSortDate(String sortDate) {
        this.sortDate = sortDate;
    }

    public String getSourceTitle() {
        return sourceTitle;
    }

    public void setSourceTitle(String sourceTitle) {
        this.sourceTitle = sourceTitle;
    }
}
