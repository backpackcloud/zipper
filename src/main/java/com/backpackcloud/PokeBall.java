package com.backpackcloud;

import com.backpackcloud.spectaculous.Action;

/**
 * Implements the famous Pokémon Exception Handling design pattern.
 *
 * @author Marcelo Guimarães
 */
public final class PokeBall {

  private PokeBall() {

  }

  public static void gottaCatchEmAll(Action action) {
    try {
      action.run();
    } catch (Throwable e) {
      // gotta catch'em all!!
    }
  }

}
