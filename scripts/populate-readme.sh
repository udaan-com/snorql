#!/bin/bash

cd ./doc/docs/sections/ || exit
OUTPUT_DIR="../../../README.md"

cat ./badges.md > ${OUTPUT_DIR}
cat ./title.md >> ${OUTPUT_DIR}
cat ./contents.md >> ${OUTPUT_DIR}

echo "<!-- ABOUT THE PROJECT -->" >> ${OUTPUT_DIR}
echo "## About The Project" >> ${OUTPUT_DIR}
cat ./about/readme.md >> ${OUTPUT_DIR}

echo "<!-- USAGE EXAMPLES -->" >> ${OUTPUT_DIR}
echo "## Usage" >> ${OUTPUT_DIR}
echo "### How to integrate snorql in your project" >> ${OUTPUT_DIR}
cat ./integrate-snorql-to-project.md >> ${OUTPUT_DIR}

echo "### Enable 'snorql-extensions' in your project (optional)" >> ${OUTPUT_DIR}
cat ./enable-snorql-extensions/readme.md >> ${OUTPUT_DIR}

echo "## Build your own custom metrics using snorql" >> ${OUTPUT_DIR}
cat ./build-custom-metrics.md >> ${OUTPUT_DIR}

echo "<!-- KOTLIN DOCUMENTATION -->" >> ${OUTPUT_DIR}
echo "## Kotlin Documentation" >> ${OUTPUT_DIR}
cat ./kotlin-doc/readme.md >> ${OUTPUT_DIR}

echo "<!-- ROADMAP -->" >> ${OUTPUT_DIR}
echo "## Roadmap" >> ${OUTPUT_DIR}
cat ./roadmap.md >> ${OUTPUT_DIR}

echo "<!-- CONTRIBUTING -->" >> ${OUTPUT_DIR}
echo "## Contributing" >> ${OUTPUT_DIR}
cat ../../../CONTRIBUTING.md >> ${OUTPUT_DIR}

echo "<!-- LICENSE -->" >> ${OUTPUT_DIR}
echo "## License" >> ${OUTPUT_DIR}
cat ./license-details.md >> ${OUTPUT_DIR}

echo "<!-- CONTACT -->" >> ${OUTPUT_DIR}
echo "## Contact" >> ${OUTPUT_DIR}
cat ./contacts.md >> ${OUTPUT_DIR}

echo "<!-- ACKNOWLEDGMENTS -->" >> ${OUTPUT_DIR}
echo "## Acknowledgments" >> ${OUTPUT_DIR}
cat ./acknowledgements.md >> ${OUTPUT_DIR}

