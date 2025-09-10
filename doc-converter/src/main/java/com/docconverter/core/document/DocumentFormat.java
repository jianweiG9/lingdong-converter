/*
 * Document Converter - A local document converter based on LibreOffice
 * Migrated from JODConverter project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.docconverter.core.document;

import java.util.*;
import java.util.stream.Collectors;

import com.docconverter.core.util.AssertUtils;

/**
 * Contains the required information used to deal with a specific document format.
 * 
 * <p>A document format defines the properties needed to load and store documents
 * in LibreOffice, including file extensions, media types, and format-specific properties.
 * 
 * @author Document Converter Team
 * @since 1.0.0
 */
public final class DocumentFormat {

    private final String name;
    private final List<String> extensions;
    private final String mediaType;
    private final DocumentFamily inputFamily;
    private final Map<String, Object> loadProperties;
    private final Map<DocumentFamily, Map<String, Object>> storeProperties;

    /**
     * Creates a new builder instance.
     *
     * @return A new builder instance
     */
    public static Builder builder() {
        return new Builder();
    }

    /**
     * Creates a new builder instance from the specified format.
     *
     * @param sourceFormat The source document format
     * @return A new builder instance
     * @throws IllegalArgumentException if sourceFormat is null
     */
    public static Builder builder(final DocumentFormat sourceFormat) {
        return new Builder().from(sourceFormat);
    }

    /**
     * Creates a new modifiable {@link DocumentFormat} from the specified format.
     *
     * @param sourceFormat The source document format
     * @return A {@link DocumentFormat}, which will be modifiable, unlike the default document formats
     * @throws IllegalArgumentException if sourceFormat is null
     */
    public static DocumentFormat copy(final DocumentFormat sourceFormat) {
        return builder(sourceFormat).unmodifiable(false).build();
    }

    /**
     * Creates a new unmodifiable {@link DocumentFormat} from the specified format.
     *
     * @param sourceFormat The source document format
     * @return A {@link DocumentFormat}, which will be unmodifiable, like the default document formats
     * @throws IllegalArgumentException if sourceFormat is null
     */
    public static DocumentFormat unmodifiableCopy(final DocumentFormat sourceFormat) {
        return builder(sourceFormat).unmodifiable(true).build();
    }

    /**
     * Creates a new document format with the specified properties.
     *
     * @param name The name of the format
     * @param extensions The file name extensions of the format
     * @param mediaType The media type (mime type) of the format
     * @param inputFamily The input {@link DocumentFamily} of the document
     * @param loadProperties The properties required to load(open) a document of this format
     * @param storeProperties The properties required to store(save) a document of this format to a
     *     document of another family
     * @param unmodifiable {@code true} if the created document format cannot be modified after
     *     creation, {@code false} otherwise
     */
    private DocumentFormat(
            final String name,
            final Collection<String> extensions,
            final String mediaType,
            final DocumentFamily inputFamily,
            final Map<String, Object> loadProperties,
            final Map<DocumentFamily, Map<String, Object>> storeProperties,
            final boolean unmodifiable) {

        checkName(name);
        AssertUtils.notNull(extensions, "extensions must not be null");
        AssertUtils.notBlank(mediaType, "mediaType must not be null nor blank");

        this.name = name;
        this.extensions = new ArrayList<>(extensions);
        this.mediaType = mediaType;
        this.inputFamily = inputFamily;
        
        if (loadProperties == null) {
            this.loadProperties = null;
        } else {
            this.loadProperties = unmodifiable
                    ? Collections.unmodifiableMap(new HashMap<>(loadProperties))
                    : new HashMap<>(loadProperties);
        }
        
        if (storeProperties == null) {
            this.storeProperties = null;
        } else {
            final Map<DocumentFamily, Map<String, Object>> familyMap = new EnumMap<>(DocumentFamily.class);
            storeProperties.forEach((family, props) ->
                    familyMap.put(family, unmodifiable
                            ? Collections.unmodifiableMap(new HashMap<>(props))
                            : new HashMap<>(props)));
            this.storeProperties = unmodifiable ? Collections.unmodifiableMap(familyMap) : familyMap;
        }
    }

    /**
     * Gets the extension associated with the document format. It will return the same extension as
     * {@code #getExtensions().get(0)}.
     *
     * @return A string that represents an extension
     */
    public String getExtension() {
        return extensions.get(0);
    }

    /**
     * Gets the file name extensions of the document format.
     *
     * @return A list of string that represents the extensions
     */
    public List<String> getExtensions() {
        return extensions;
    }

    /**
     * Gets the input DocumentFamily of the document format.
     *
     * @return The input DocumentFamily of the document format
     */
    public DocumentFamily getInputFamily() {
        return inputFamily;
    }

    /**
     * Gets the properties required to load(open) a document of this format.
     *
     * @return A map containing the properties to apply when loading a document of this format
     */
    public Map<String, Object> getLoadProperties() {
        return loadProperties;
    }

    /**
     * Gets the media (mime) type of the format.
     *
     * @return A string that represents the media type
     */
    public String getMediaType() {
        return mediaType;
    }

    /**
     * Gets the name of the format.
     *
     * @return A string that represents the name of the format
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the properties required to store(save) a document of this format to a document of
     * supported families.
     *
     * @return A DocumentFamily/Map pair containing the properties to apply when storing a document of
     *     this format, by DocumentFamily
     */
    public Map<DocumentFamily, Map<String, Object>> getStoreProperties() {
        return storeProperties;
    }

    /**
     * Gets the properties required to store(save) a document to this format from a document of the
     * specified family.
     *
     * @param family The DocumentFamily for which the properties are get
     * @return A map containing the properties to apply when storing a document to this format
     */
    public Map<String, Object> getStoreProperties(final DocumentFamily family) {
        return storeProperties == null ? null : storeProperties.get(family);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName()
                + "{"
                + "name=\"" + name + "\""
                + ", extensions=" + extensions
                + ", mediaType=\"" + mediaType + "\""
                + ", inputFamily=" + inputFamily
                + ", loadProperties=" + loadProperties
                + ", storeProperties=" + storeProperties
                + '}';
    }

    /**
     * A builder for constructing a {@link DocumentFormat}.
     *
     * @see DocumentFormat
     */
    public static final class Builder {

        private String name;
        private Set<String> extensions;
        private String mediaType;
        private DocumentFamily inputFamily;
        private Map<String, Object> loadProperties;
        private Map<DocumentFamily, Map<String, Object>> storeProperties;
        private boolean unmodifiable = true;

        // Private constructor so only DocumentFormat can initialize an instance of this builder.
        private Builder() {
            super();
        }

        /**
         * Creates the converter specified by this builder.
         *
         * @return The converter that is specified by this builder
         */
        public DocumentFormat build() {
            return new DocumentFormat(
                    name, extensions, mediaType, inputFamily, loadProperties, storeProperties, unmodifiable);
        }

        /**
         * Initializes the builder by copying the properties of the specified document format.
         *
         * @param sourceFormat The source document format; cannot be null
         * @return This builder instance
         */
        public Builder from(final DocumentFormat sourceFormat) {
            AssertUtils.notNull(sourceFormat, "sourceFormat must not be null");
            this.name = sourceFormat.getName();
            this.extensions = new LinkedHashSet<>(sourceFormat.getExtensions());
            this.mediaType = sourceFormat.getMediaType();
            this.inputFamily = sourceFormat.getInputFamily();
            this.loadProperties = sourceFormat.getLoadProperties() == null
                    ? null
                    : new HashMap<>(sourceFormat.getLoadProperties());
            if (sourceFormat.getStoreProperties() != null) {
                this.storeProperties = new EnumMap<>(DocumentFamily.class);
                sourceFormat.getStoreProperties()
                        .forEach((family, propMap) -> this.storeProperties.put(family, new HashMap<>(propMap)));
            }
            return this;
        }

        /**
         * Specifies the extension associated with the document format.
         *
         * @param extension The extension; cannot be null
         * @return This builder instance
         */
        public Builder extension(final String extension) {
            AssertUtils.notBlank(extension, "extension must not be null nor blank");
            if (this.extensions == null) {
                this.extensions = new LinkedHashSet<>();
            }
            this.extensions.add(extension);
            return this;
        }

        /**
         * Specifies the input (when a document is loaded) DocumentFamily associated with the document
         * format.
         *
         * @param inputFamily The DocumentFamily; may be null
         * @return This builder instance
         */
        public Builder inputFamily(final DocumentFamily inputFamily) {
            this.inputFamily = inputFamily;
            return this;
        }

        /**
         * Adds a property to the builder that will be applied when loading (open) a document of this
         * format.
         *
         * @param name The property name; cannot be null
         * @param value The property value; may be null. If null, it will REMOVE the property from the map
         * @return This builder instance
         */
        public Builder loadProperty(final String name, final Object value) {
            checkName(name);

            if (value == null) {
                // Remove the property if the value is null.
                if (loadProperties != null) {
                    loadProperties.remove(name);
                    if (loadProperties.isEmpty()) {
                        loadProperties = null;
                    }
                }
            } else {
                // Add the property if a value is given.
                if (loadProperties == null) {
                    loadProperties = new HashMap<>();
                }
                loadProperties.put(name, value);
            }
            return this;
        }

        /**
         * Specifies the media (mime) type of the document format.
         *
         * @param mediaType A string that represents the media type; cannot be null
         * @return This builder instance
         */
        public Builder mediaType(final String mediaType) {
            AssertUtils.notBlank(mediaType, "mediaType must not be null nor blank");
            this.mediaType = mediaType;
            return this;
        }

        /**
         * Specifies the name of the document format.
         *
         * @param name The name of the document format; cannot be null
         * @return This builder instance
         */
        public Builder name(final String name) {
            checkName(name);
            this.name = name;
            return this;
        }

        /**
         * Specifies whether the document format is unmodifiable after creation. Default to {@code true}.
         *
         * @param unmodifiable {@code true} if the created document format cannot be modified after
         *     creation, {@code false} otherwise
         * @return This builder instance
         */
        public Builder unmodifiable(final boolean unmodifiable) {
            this.unmodifiable = unmodifiable;
            return this;
        }

        /**
         * Adds a property to the builder that will be applied when storing (save) a document to this
         * format from a document of the specified family.
         *
         * @param documentFamily The document family of the source (loaded) document; cannot be null
         * @param name The property name; cannot be null
         * @param value The property value; may be null. If null, it will REMOVE the property from the map
         * @return This builder instance
         */
        public Builder storeProperty(
                final DocumentFamily documentFamily,
                final String name,
                final Object value) {

            AssertUtils.notNull(documentFamily, "documentFamily must not be null");
            checkName(name);

            if (value == null) {
                // Remove the property if the value is null.
                return removeStoreProperty(documentFamily, name);
            }

            // Add the property if a value is given.
            if (storeProperties == null) {
                storeProperties = new EnumMap<>(DocumentFamily.class);
            }
            storeProperties.computeIfAbsent(documentFamily, key -> new HashMap<>()).put(name, value);
            return this;
        }

        private Builder removeStoreProperty(final DocumentFamily documentFamily, final String name) {
            // Remove the property if the value is null.
            if (storeProperties != null) {
                final Map<String, Object> props = storeProperties.get(documentFamily);
                if (props != null) {
                    props.remove(name);
                    if (props.isEmpty()) {
                        storeProperties.remove(documentFamily);
                    }
                    if (storeProperties.isEmpty()) {
                        storeProperties = null;
                    }
                }
            }
            return this;
        }
    }

    private static void checkName(final String name) {
        AssertUtils.notBlank(name, "name must not be null nor blank");
    }
}