/*
 * Copyright (C) 2015 The Common CLI viewer interface Project
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

#ifndef __ST_BYTE_BUFFER_IMAGE_H__
#define __ST_BYTE_BUFFER_IMAGE_H__

#include "StEnv.h"

struct RGBA_8888
{
    uint8_t r;
    uint8_t g;
    uint8_t b;
    uint8_t a;
};

struct CA_8888
{
    unsigned int c:24;
    unsigned int a:8;
};

union pixel
{
    RGBA_8888 rgba;
    CA_8888 ca;
    uint32_t value;
};

class JByteBufferImage: public JBuffer<pixel>
{
protected:
    int width;
    int height;
    int pixelsCount;

public:
    JByteBufferImage(JNIEnv *jenv, jobject buffer, int width, int height)
        : JBuffer(jenv, buffer), width(width), height(height), pixelsCount(width * height)
    {
    }

    JByteBufferImage(JByteBufferImage&) = delete;
    JByteBufferImage(JByteBufferImage&&) = default;
    JByteBufferImage& operator=(JByteBufferImage const&) = delete;

public:

    pixel* begin()
    {
        return asBuffer();
    }
    pixel* end()
    {
        return asBuffer() + pixelsCount;
    }
    int getPixelCount()
    {
        return pixelsCount;
    }
};
#endif
