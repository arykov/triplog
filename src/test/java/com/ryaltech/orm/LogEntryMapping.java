package com.ryaltech.orm;

import com.ryaltech.orm.Mapping;

public class LogEntryMapping extends Mapping<LogEntry> {

    public LogEntryMapping() {
        super(LogEntry.class, "LogEntry");
        setIdColumn("entry_id");
        add("message", "message_col");
    }
}
