package com.pinkproject._core.utils;

import com.fasterxml.jackson.core.SerializableString;
import com.fasterxml.jackson.core.io.CharacterEscapes;
import com.fasterxml.jackson.core.io.SerializedString;

public class CustomCharacterEscapes extends CharacterEscapes {
    private final int[] asciiEscapes;

    public CustomCharacterEscapes() {
        // 기본 ASCII 탈출 시퀀스 설정
        asciiEscapes = CharacterEscapes.standardAsciiEscapesForJSON();
        // 추가로 escape 처리할 문자
        asciiEscapes['<'] = CharacterEscapes.ESCAPE_STANDARD;
        asciiEscapes['>'] = CharacterEscapes.ESCAPE_STANDARD;
        asciiEscapes['&'] = CharacterEscapes.ESCAPE_STANDARD;
        asciiEscapes['\''] = CharacterEscapes.ESCAPE_STANDARD;
    }

    @Override
    public int[] getEscapeCodesForAscii() {
        return asciiEscapes;
    }

    @Override
    public SerializableString getEscapeSequence(int ch) {
        return new SerializedString(Character.toString((char) ch));// no further escaping (beyond ASCII chars) needed
    }
}
