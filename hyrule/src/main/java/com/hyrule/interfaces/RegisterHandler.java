package com.hyrule.interfaces;

import java.io.BufferedWriter;

public interface RegisterHandler {
    boolean process(String linea, BufferedWriter logWriter);

}
