cmake_minimum_required(VERSION 3.4.1)

add_library(validation SHARED validation.c)

include_directories(${CMAKE_SOURCE_DIR}/include)

find_library(log-lib log)

target_link_libraries(validation ${log-lib})
