# Branchsim

A branch predicting performance simulator. A lab project of CS 203 Advanced Computer Architecture.

The homeowrk requirement is in the [link](https://piazza.com/class_profile/get_resource/k4wa1sxbn9cji/k5w0dc6hxpr1zh).

## How To Compile

```shell
cd Branchsim
javac Branchsim.java
```

Then run with 4 parameters:
```shell
java Branchsim [FILE_NAME] [M_HISTORY_BIT] [N_BIT_PREDICTOR] [BITS_TO_INDEX]
```

Examples:

```shell
java Branchsim gcc-8M.txt 6 1 12  // 6 history bits, 1-bit predictor, 12 bits to index
java Branchsim gcc-8M.txt 6 2 12

java Branchsim gcc-8M.txt 6 1 8
java Branchsim gcc-8M.txt 6 2 12
```

Notice that:

- `FILE_NAME` ∈ {gcc-8M.txt, gcc-10k.TXT}
- `M_HISTORY_BIT` ∈ [0, 12]
- `N_BIT_PREDICTOR`∈ [1, 2]
- `BITS_TO_INDEX` ∈ [4, 12]
