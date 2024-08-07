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
    src/main/scala/Ebreak.scala
)
set(EXT_VERILOG
    src/main/resources/verilog/Ebreak.v
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
foreach(FILE ${EXT_VERILOG})
    file(TIMESTAMP ${FILE} FILE_TIMESTAMP)
    if(${FILE_TIMESTAMP} STRGREATER ${SCALA_SOURCE_TIMESTAMP})
        set(SCALA_SOURCE_TIMESTAMP ${FILE_TIMESTAMP})
    endif()
endforeach()

# check if the generated Verilog files are up-to-date
if (NOT EXISTS ${VERILOG_SRC_LIST})
    set(ELABORATE_OUTDATED TRUE)
    message(STATUS "Verilog Files are outdated")
else()
    file(TIMESTAMP ${VERILOG_SRC_LIST} VERILOG_SRC_TIMESTAMP)
    if (${SCALA_SOURCE_TIMESTAMP} STRGREATER ${VERILOG_SRC_TIMESTAMP})
        set(ELABORATE_OUTDATED TRUE)
        message(STATUS "Verilog Files are outdated")
    else()
        set(ELABORATE_OUTDATED FALSE)
        message(STATUS "Verilog Files are up-to-date")
    endif()
endif()
# if the generated Verilog files are outdated, re-run the Elaborate command
if (${ELABORATE_OUTDATED})
    message(STATUS "Running Elaborate command")
    execute_process(
        COMMAND mkdir -p ${VERILOG_SRC_DIR}
        COMMAND mill ${MILL_VERSION} -i ${PROJECT_NAME}.runMain Elaborate ${FIRRTL_ARG}
        WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
        RESULT_VARIABLE ELABORATE_RESULT
    )
    if (NOT ${ELABORATE_RESULT} EQUAL 0)
        message(FATAL_ERROR "Elaborate command failed with exit code ${ELABORATE_RESULT}")
    endif()
endif()

add_custom_command(
    OUTPUT ${VERILOG_SRC_LIST}
    COMMAND mkdir -p ${VERILOG_SRC_DIR}
    COMMAND mill ${MILL_VERSION} -i ${PROJECT_NAME}.runMain Elaborate ${FIRRTL_ARG}
    DEPENDS ${ELABORATE_SOURCE} ${CHISEL_SOURCE}
    WORKING_DIRECTORY ${CMAKE_CURRENT_SOURCE_DIR}
)

add_custom_target(elaborate
    DEPENDS ${VERILOG_SRC_LIST}
)

file(STRINGS ${VERILOG_SRC_LIST} VERILOG_FILE_NAME)
foreach(FILE ${VERILOG_FILE_NAME})
    list(APPEND VERILOG_SOURCE ${VERILOG_SRC_DIR}/${FILE})
endforeach()


add_executable(core_sim
    src/main/resources/cpp/core_sim.cpp
)
add_dependencies(core_sim elaborate)
verilate(core_sim
    PREFIX VCore
    TOP_MODULE Core
    SOURCES ${VERILOG_SOURCE}
    VERILATOR_ARGS --trace -j 8 
)