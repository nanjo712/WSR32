cmake_minimum_required(VERSION 3.29)

project(WSR32)

set(CMAKE_CXX_STANDARD 17)
set(CMAKE_CXX_STANDARD_REQUIRED ON)
set(CMAKE_CXX_EXTENSIONS OFF)

set(EXPORT_COMPILE_COMMANDS ON)

set(ELABORATE_SOURCE src/main/scala/Elaborate.scala)

set(CHISEL_SOURCE
    src/main/scala/ALU.scala 
    src/main/scala/Core.scala 
    src/main/scala/EXU.scala 
    src/main/scala/IDU.scala 
    src/main/scala/IFU.scala 
    src/main/scala/ImmGenerator.scala 
    src/main/scala/PCRegister.scala 
    src/main/scala/RegisterFile.scala
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
    COMMAND mill ${MILL_VERSION} -i ${PROJECT_NAME}.runMain Elaborate ${FIRRTL_ARG}
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
    SOURCES ${CHISEL_SOURCE} ${ELABORATE_SOURCE}
)


foreach(FILE ${CHISEL_SOURCE})
    get_filename_component(FILE_NAME ${FILE} NAME_WE)
    list(APPEND VERILOG_SOURCE ${VERILOG_SRC_DIR}/${FILE_NAME}.sv)    
endforeach()

set(VERILATOR_ARG
    --top-module Core
    --trace
    -j 8
)
add_custom_target(simulate ALL
    COMMAND verilator ${VERILATOR_ARG} ${VERILOG_SOURCE} --cc -o ${PROJECT_NAME}
    DEPENDS elaborate
)