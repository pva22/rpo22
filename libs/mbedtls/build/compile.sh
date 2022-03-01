#!/bin/bash

#ABI=armeabi-v7a
#ABI=x86
#ABI=arm64-v8a
ABI=x86_64

ANDROID_NDK=/Users/19690612/Library/Android/sdk/ndk/23.1.7779620
TOOL_CHAIN=${ANDROID_NDK}/build/cmake/android.toolchain.cmake
CMAKE=/usr/local/bin/cmake

mkdir -p ${ABI}
cd ${ABI}
${CMAKE} ../../mbedtls -DCMAKE_SYSTEM_NAME=Android -DCMAKE_SYSTEM_VERSION=22 \
-DANDROID_ABI=${ABI} -DCMAKE_TOOLCHAIN_FILE=${TOOL_CHAIN} \
-DUSE_SHARED_MBEDTLS_LIBRARY=On -DENABLE_TESTING=Off
${CMAKE} --build .
