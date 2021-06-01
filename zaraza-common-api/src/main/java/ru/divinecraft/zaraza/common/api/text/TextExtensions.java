/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package ru.divinecraft.zaraza.common.api.text;

import lombok.experimental.UtilityClass;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import org.jetbrains.annotations.NotNull;
import ru.progrm_jarvis.ultimatemessenger.format.model.TextModel;

/**
 * Utility methods to use as extensions to {@link TextModel} API.
 */
@UtilityClass
public class TextExtensions {

    /**
     * Converts the specified text model to Adventure API component.
     *
     * @param textModel text model whose text for the given target should be converted to a component
     * @param target target for which to create a text
     * @param <T> type of the target
     * @return component created from the text model for the given target
     */
    public <T> @NotNull Component toComponentOf(final @NotNull TextModel<? super T> textModel,
                                                final @NotNull T target) {
        return GsonComponentSerializer.gson().deserialize(textModel.getText(target));
    }
}
