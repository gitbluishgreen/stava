#!/bin/bash
# Sample script to be used to run the project on a benchmark.
# Set the paths according to your installation. All paths must be full paths.
# Arguments: 1) name of benchmark
# Installed path of Java 8 JDK
java_install_path="/home/cs17b006/jdk1.8.0_291"
# java_install_path=""
# java_install_path="/wd/users/t17041/benchmarks/jdk-11.0.8/"
# Path to the directory containing all benchmarks. The subdirectories here must
# contain individual benchmarks 
benchmarks_base_path=/home/cs17b006/stava-master/Benchmarks
# Path to stava repository
stava_path=/home/cs17b006/stava-master
# The soot jar to be used.
soot_path=${stava_path}/soot/sootclasses-trunk-jar-with-dependencies.jar
# soot_path="/home/dj/github/soot/target/sootclasses-trunk-jar-with-dependencies.jar"

stava_run=${stava_path}/src/
# The directory inside which stava will output the results.
output_base_path=${stava_path}/output_dir
java_compiler=${java_install_path}/bin/javac
java_vm=${java_install_path}/bin/java

find  ${stava_path}/src -type f -name '*.class' -delete
echo compiling...
$java_compiler -cp $soot_path:${stava_path}/src:${stava_path}/Benchmarks/out ${stava_path}/src/main/Main.java
echo compiled!

execute () {
    echo launching...
    echo $1 $2 $3
    $java_vm -Xmx20g -classpath $soot_path:$stava_run main.Main $java_install_path true $1 $2 $3
}
clean () {
    echo clearing output_files...
    find $1 -type f -name '*.res' -delete
    find $1 -type f -name '*.info' -delete    
    find $1 -type f -name 'stats.txt' -delete
}
if [[ $1 == "dacapo" ]]; then
    benchmark_path="${benchmarks_base_path}/dacapo"
    output_path="${output_base_path}/dacapo"
    main_class="Harness"
elif [[ $1 == "jbb" ]]; then
    benchmark_path="${benchmarks_base_path}/SPECjbb2005"
    output_path="${output_base_path}/jbb/"
    main_class="spec.jbb.JBBmain"
elif [[ $1 == "jgfall" ]]; then
    for dir in ${benchmarks_base_path}/jgf/JGF*
    do
        lib=${dir##*/}
        echo $lib
        output_path="${output_base_path}/jgf/${lib}"
        clean $output_path
        execute $dir $lib $output_path
    done
    exit 0
elif [[ $1 == "barrier" ]]; then
    benchmark_path="${benchmarks_base_path}/jgf/JGFBarrierBench"
    output_path="${output_base_path}/jgf/JGFBarrierBench"
    main_class="JGFBarrierBench"
elif [[ $1 == "montecarlo" ]]; then
    benchmark_path="${benchmarks_base_path}/jgf/JGFMonteCarloBenchSizeA"
    output_path="${output_base_path}/jgf/JGFMonteCarloBenchSizeA"
    main_class="JGFMonteCarloBenchSizeA"
elif [[ $1 == "raytracer" ]]; then
    benchmark_path="${benchmarks_base_path}/jgf/JGFRayTracerBenchSizeA"
    output_path="${output_base_path}/jgf/JGFRayTracerBenchSizeA"
    main_class="JGFRayTracerBenchSizeA"
elif [[ $1 == "crypt" ]]; then
    benchmark_path="${benchmarks_base_path}/jgf/JGFCryptBenchSizeA"
    output_path="${output_base_path}/jgf/JGFCryptBenchSizeA"
    main_class="JGFCryptBenchSizeA"
else
    echo path not recognised
    exit 0
fi
clean $output_path
find  ${stava_path}/src -type f -name '*.class' -delete
echo compiling...
$java_compiler -cp $soot_path:${stava_path}/src ${stava_path}/src/main/Main.java
echo compiled!
echo launching...
if [[ $1 == "dacapo" ]]; then
    a=("fop-small" "eclipse-small" "batik-small" "fop-default" "eclipse-large" "eclipse-default" "batik-default")
    a=("sunflow-small" "sunflow-default" "sunflow-large")
    for benchmark in ../Benchmarks/out/*
    # #After dumping with tamiflex
    do
        #benchmark="../Benchmarks/out/$bm"
        echo $benchmark
        benchmark_name=$(echo $benchmark | cut --delimiter='/' --fields=4)
        if [[ ! -f $benchmark/refl.log ]]; then
            echo "Benchmark $benchmark_name does not have a reflection log!"
        else
            output_path="../output_dir/dacapo/$benchmark_name"
            mkdir -p $output_path
            benchmark_path="${benchmarks_base_path}/out/${benchmark_name}"
            execute $benchmark_path $main_class $output_path
        fi
    done
else
    execute $benchmark_path $main_class $output_path
fi