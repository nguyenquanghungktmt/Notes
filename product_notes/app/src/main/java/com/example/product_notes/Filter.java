package com.example.product_notes;

public interface Filter {
    void filterNone();
    void filterText();
    void filterChecklist();
    void filterLatestDate();
    void filterTag();
    void filterReminder();
}
