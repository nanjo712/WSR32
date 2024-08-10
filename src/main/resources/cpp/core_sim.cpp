#include <cstdint>
#include <fstream>
#include <iostream>
#include <memory>

#include "VCore.h"
#include "VCore__Dpi.h"
#include "verilated.h"
#include "verilated_vcd_c.h"

uint32_t counter = 0;
std::vector<uint32_t> memory = {0x02a00093, 0x00100113, 0xfd400193, 0x00100073};

enum CORE_STATUS
{
    STATUS_OK,
    STATUS_ERROR,
    STATUS_STOP
};

CORE_STATUS status = STATUS_OK;

void invalid_instruction_handler()
{
    status = STATUS_ERROR;
    std::cout << "Invalid instruction" << std::endl;
}

void ebreak_handler() { status = STATUS_STOP; }

void tick(VCore& top, VerilatedVcdC& trace)
{
    top.clock = 0;
    top.eval();
    trace.dump(counter++);

    top.clock = 1;
    top.eval();
    trace.dump(counter++);
}

bool load_img(const std::string& filename)
{
    std::ifstream file(filename, std::ios::binary);
    if (!file.is_open())
    {
        std::cerr << "Failed to open file: " << filename << std::endl;
        return false;
    }

    file.seekg(0, std::ios::end);
    auto size = file.tellg();
    file.seekg(0, std::ios::beg);
    memory.resize(size / 4 + 1);
    file.read(reinterpret_cast<char*>(memory.data()), size);
    file.close();
    return true;
}

int main(int argc, char** argv)
{
    if (argc < 2 || !load_img(argv[1]))
    {
        std::cout << "Using default image" << std::endl;
    }
    else
    {
        std::cout << "Using image: " << argv[1] << std::endl;
    }
    auto context = std::make_unique<VerilatedContext>();
    context->commandArgs(argc, argv);
    context->traceEverOn(true);

    auto trace = std::make_unique<VerilatedVcdC>();
    auto top = std::make_unique<VCore>(context.get());
    top->trace(trace.get(), 0);
    trace->open("core.vcd");

    top->reset = 1;
    for (int i = 0; i < 10; i++)
    {
        tick(*top, *trace);
    }

    top->reset = 0;

    for (int i = 0; status == STATUS_OK; i++)
    {
        assert(i < sizeof(memory) / sizeof(memory[0]));
        top->io_instruction = memory[i];
        tick(*top, *trace);
    }

    if (status == STATUS_ERROR)
    {
        std::cout << "Hit BAD Trap" << std::endl;
    }
    else
    {
        std::cout << "Hit Ebreak" << std::endl;
    }

    std::cout << "Simulation stop" << std::endl;
    std::cout << "Core Status: " << status << std::endl;
    return !(status == STATUS_STOP);
}