cmake_minimum_required(VERSION 3.29)

project(WSR32)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

set(EXPORT_COMPILE_COMMANDS ON)

find_package(verilator HINTS $ENV{VERILATOR_ROOT})

set(CHISEL_SOURCE
    src/main/ALU.scala 
    src/main/Core.scala 
    src/main/Elaborate.scala
    src/main/EXU.scala 
    src/main/IDU.scala 
    src/main/IFU.scala 
    src/main/ImmGenerator.scala 
    src/main/PCRegister.scala 
    src/main/RegisterFile.scala
)

set(MILL_VERSION "--mill-version" "0.11.11")
set(VERILOG_SRC_DIR ${CMAKE_CURRENT_BINARY_DIR}/generated)
set(FIRRTL_ARG 
    "--target-dir" ${VERILOG_SRC_DIR} 
    "--full-stacktrace"
    "--split-verilog"
)

add_custom_target(elaborate 
    COMMAND mkdir -p ${VERILOG_SRC_DIR}
    COMMAND mill -i ${MILL_VERSION} WSR32.runMain Elaborate ${FIRRTL_ARG}
    DEPENDS ${CHISEL_SOURCE}
)

foreach(FILE ${CHISEL_SOURCE})
    get_filename_component(FILE_NAME ${FILE} NAME_WE)
    list(APPEND VERILOG_SOURCE ${VERILOG_SRC_DIR}/${FILE_NAME}.v)    
endforeach()

set(VERILATOR_ARG
    -Wall 
    --trace
    -j8
)


