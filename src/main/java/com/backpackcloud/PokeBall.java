package com.backpackcloud;

import com.backpackcloud.spectaculous.Action;

/**
 * Implements the famous Pokémon Exception Handling design pattern.
 *
 * @author Marcelo Guimarães
 */
public interface PokeBall {

  void execute(Action action);

  static PokeBall gottaCatchEmAll() {
    return action -> {
      try {
        action.run();
      } catch (Throwable e) {
        // gotta catch'em all!!
      }
    };
  }

}
