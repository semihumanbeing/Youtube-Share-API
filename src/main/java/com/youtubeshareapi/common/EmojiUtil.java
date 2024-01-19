package com.youtubeshareapi.common;

import java.util.Random;

public class EmojiUtil {
  private static final String[] EMOJI_RANGES = {
      "\uD83E\uDD86", "\uD83E\uDD85", // 🦆 (오리), 🦅 (독수리)
      "\uD83E\uDD89", "\uD83D\uDD4A", // 🦉 (부엉이), 🕊️ (비둘기)
      "\uD83E\uDD9C", "\uD83E\uDD9A", // 🦜 (앵무새), 🦚 (공작)
      "\uD83D\uDC26", "\uD83D\uDC24", // 🐦 (새), 🐤 (병아리)

      "\uD83D\uDC0D", "\uD83D\uDC22", // 🐍 (뱀), 🐢 (거북)
      "\uD83D\uDC32", "\uD83E\uDD8E", // 🐲 (용), 🦎 (도마뱀)
      "\uD83E\uDDA0", "\uD83D\uDC1A", // 🦠 (미생물), 🐚 (조개)

      "\uD83D\uDC1F", "\uD83D\uDC20", // 🐟 (물고기), 🐠 (열대어)
      "\uD83D\uDC21", "\uD83D\uDC27", // 🐡 (복어), 🐧 (펭귄)
      "\uD83D\uDC19", "\uD83E\uDD80", // 🐙 (문어), 🦀 (게)
      "\uD83E\uDD81", "\uD83E\uDD82",  // 🦑 (오징어), 🦂 (전갈)

      "\uD83D\uDE02", "\uD83D\uDE03", // 😂 (웃음 눈물), 😃 (밝은 웃음)
      "\uD83D\uDE06", "\uD83D\uDE05", // 😆 (웃음), 😅 (땀 웃음)
      "\uD83D\uDE03", "\uD83D\uDE0A", // 😃 (밝은 웃음), 😊 (미소)
      "\uD83D\uDE0D", "\uD83D\uDE18",  // 😍 (하트 눈), 😘 (키스 미소)
      "\uD83D\uDC83", "\uD83D\uDD7A", // 💃 (춤추는 여성), 🕺 (춤추는 남성)
      "\uD83D\uDE08", "\uD83D\uDC7B",  // 😈 (악마 웃는 얼굴), 👻 (유령)

      "\uD83C\uDFA4", "\uD83C\uDFA7", // 🎤 (마이크), 🎧 (헤드폰)
      "\uD83C\uDFB8", "\uD83C\uDFBA", // 🎸 (기타), 🎺 (트럼펫)
      "\uD83C\uDFBB", "\uD83C\uDFB7", // 🎻 (바이올린), 🎷 (색소폰)
      "\uD83D\uDD25", "\uD83C\uDFB2" // 🔥 (불꽃)
  };

  private static final Random random = new Random();

  public static String getRandomEmoji() {
    int randomIndex = random.nextInt(EMOJI_RANGES.length);
    return EMOJI_RANGES[randomIndex];
  }

}
