package br.edu.ifba.inf008.plugins.books.domain.entities;

import br.edu.ifba.inf008.core.domain.interfaces.Nameable;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableColumnSize;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableIgnore;
import br.edu.ifba.inf008.core.ui.components.table.annotations.TableLabel;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "books")
public class Book implements Nameable {

    /**
     * Represents a book in the system.
     */

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id", nullable = false, unique = true)
    @TableLabel("#")
    @TableColumnSize(50)
    private Long bookId;

    @Column(nullable = false)
    @TableColumnSize(220)
    private String title;

    @Column(nullable = false)
    @TableLabel("Author")
    @TableColumnSize(160)
    private String author;

    @Column(name = "isbn", nullable = false, unique = true)
    @TableLabel("ISBN")
    @TableColumnSize(150)
    private String isbn;

    @Column(name = "published_year")
    @TableIgnore
    private Integer publishedYear;

    @Column(name = "copies_available", columnDefinition = "INT DEFAULT 0")
    @TableIgnore
    private Integer copiesAvailable = 0;

    @Column(name = "deactivated_at", columnDefinition = "TIMESTAMP DEFAULT NULL")
    @TableIgnore
    private LocalDateTime deactivatedAt;

    public Book() {
    }

    public Book(String title, String author, String isbn, Integer publishedYear,
            Integer copiesAvailable) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.publishedYear = publishedYear;
        this.copiesAvailable = copiesAvailable;
    }

    public Long getBookId() {
        return bookId;
    }

    public void setBookId(Long bookId) {
        this.bookId = bookId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public Integer getPublishedYear() {
        return publishedYear;
    }

    public void setPublishedYear(Integer publishedYear) {
        this.publishedYear = publishedYear;
    }

    public Integer getCopiesAvailable() {
        return copiesAvailable;
    }

    public void setCopiesAvailable(Integer copiesAvailable) {
        this.copiesAvailable = copiesAvailable;
    }

    public LocalDateTime getDeactivatedAt() {
        return deactivatedAt;
    }

    public void setDeactivatedAt(LocalDateTime deactivatedAt) {
        this.deactivatedAt = deactivatedAt;
    }

    public boolean isActive() {
        return deactivatedAt == null;
    }

    @Override
    public String getName() {
        return title;
    }

    @Override
    public void setName(String name) {
        this.title = name;
    }

    @Override
    public String toString() {
        return "Book{" +
                "bookId=" + bookId +
                ", title='" + title + '\'' +
                ", author='" + author + '\'' +
                ", isbn='" + isbn + '\'' +
                ", publishedYear=" + publishedYear +
                ", copiesAvailable=" + copiesAvailable +
                ", deactivatedAt=" + deactivatedAt +
                '}';
    }

}
