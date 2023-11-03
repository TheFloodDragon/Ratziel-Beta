package com.tcoded.folialib.impl;

import com.tcoded.folialib.FoliaLib;

@SuppressWarnings("unused")
public class UnsupportedImplementation extends LegacySpigotImplementation {
    public UnsupportedImplementation(FoliaLib foliaLib) {
        super(foliaLib);
        // 取消 Log 机制
    }
}