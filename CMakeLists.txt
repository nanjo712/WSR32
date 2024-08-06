cmake_minimum_required(VERSION 3.29)

project(WSR32)

find_package(verilator HINTS $ENV{VERILATOR_ROOT})

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
set(VERILOG_SRC_LIST ${VERILOG_SRC_DIR}/filelist.f)
set(FIRRTL_ARG 
    "--target-dir" ${VERILOG_SRC_DIR} 
    "--full-stacktrace"
    "--split-verilog"
)

# get the latest timestamp of the Chisel source files
file(TIMESTAMP ${ELABORATE_SOURCE} SCALA_SOURCE_TIMESTAMP)
foreach(FILE ${CHISEL_SOURCE})
    file(TIMESTAMP ${FILE} FILE_TIMESTAMP)
    if(${FILE_TIMESTAMP} STRGREATER ${SCALA_SOURCE_TIMESTAMP})
        set(SCALA_SOURCE_TIMESTAMP ${FILE_TIMESTAMP})
    endif()
endforeach()

# check if the generated Verilog files are up-to-date
file(TIMESTAMP ${VERILOG_SRC_LIST} VERILOG_SRC_TIMESTAMP)
if(${SCALA_SOURCE_TIMESTAMP} STRGREATER ${VERILOG_SRC_TIMESTAMP})
    set(ELABORATE_OUTDATED TRUE)
    message(STATUS "Verilog Files are outdated")
else()
    set(ELABORATE_OUTDATED FALSE)
    message(STATUS "Verilog Files are up-to-date")
endif()

# if the generated Verilog files are outdated, re-run the Elaborate command
if (${ELABORATE_OUTDATED})
    message(STATUS "Re-running Elaborate command")
    execute_process(
        COMMAND mkdir -p ${VERILOG_SRC_DIR}
        COMMAND mill ${MILL_VERSION} -i ${PROJECT_NAME}.runMain Elaborate ${FIRRTL_ARG}
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
    )
endif()

# check if the command mill is available
add_custom_target(elaborate
    COMMAND mkdir -p ${VERILOG_SRC_DIR}
    COMMAND mill ${MILL_VERSION} -i ${PROJECT_NAME}.runMain Elaborate ${FIRRTL_ARG}
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
    SOURCES ${CHISEL_SOURCE} ${ELABORATE_SOURCE}
    COMMENT "Elaborating Chisel sources"
)

foreach(FILE ${CHISEL_SOURCE})
    get_filename_component(FILE_NAME ${FILE} NAME_WE)
    list(APPEND VERILOG_SOURCE ${VERILOG_SRC_DIR}/${FILE_NAME}.sv)    
endforeach()

add_executable(core_sim
    src/main/cpp/core_sim.cpp
)
add_dependencies(core_sim elaborate)
verilate(core_sim
    PREFIX VCore
    TOP_MODULE Core
    SOURCES ${VERILOG_SOURCE}
    VERILATOR_ARGS --trace -j 8 
)