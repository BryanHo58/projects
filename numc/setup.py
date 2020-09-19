from distutils.core import setup, Extension
import sysconfig

def main():
    CFLAGS = ['-g', '-Wall', '-std=c99', '-fopenmp', '-mavx', '-mfma', '-pthread', '-O3']
    LDFLAGS = ['-fopenmp']
    # Use the setup function we imported and set up the modules.
    # You may find this reference helpful: https://docs.python.org/3.6/extending/building.html
    # TODO: YOUR CODE HERE
    ext_modules = Extension('numc',
                            include_dirs = ['.'],
                            sources = ['numc.c', 'matrix.c'],
                            extra_compile_args = CFLAGS,
                            extra_link_args = LDFLAGS)
    setup(  name = 'numc',
            version = '1.0',
            author = 'Bryan Ho',
            author_email = 'bryanho58@berkeley.edu',
            description = 'numpy C module I made for CS61C',
            ext_modules = [ext_modules])

if __name__ == "__main__":
    main()
