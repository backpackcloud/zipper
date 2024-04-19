/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2022 Marcelo Guimarães
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.backpackcloud.cli;

import com.backpackcloud.cli.ui.Suggestion;
import com.backpackcloud.serializer.Serializer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;

import java.util.List;
import java.util.Optional;

public interface CLI {

  void attach(Writer writer);

  void start();

  void stop();

  void execute(String... commands);

  void execute(Writer writer, String... commands);

  List<Command> availableCommands();

  List<Suggestion> suggest(String input);

  Optional<String> leftPrompt();

  Optional<String> rightPrompt();

  default void expose(Router router) {
    Serializer serializer = Serializer.json();

    router.route(HttpMethod.POST, "/cli").handler(routingContext -> {

    });

    router.route(HttpMethod.GET, "/cli/prompts").handler(routingContext -> {
      routingContext.response()
        .setStatusCode(200)
        .putHeader("content-type", "application/json; charset=utf-8")
        .end("", "utf-8");
    });

    router.route(HttpMethod.PUT, "/cli/prompts").handler(routingContext -> {

    });
  }

}
