#!/bin/bash

set -euo pipefail

lein clean
lein compile

mkdir .tmp

cp article.html .tmp/index.html
cp dist/paint.all.js .tmp/
cp dist/paint.all.map.js .tmp/

rm -rf dist

git checkout gh-pages

cp .tmp/index.html .
cp .tmp/paint.all.js dist/
cp .tmp/paint.all.map.js dist/

git add index.html dist/paint.all.js dist/paint.all.map.js

git commit -m "Updating gh-pages"

git push origin gh-pages

git checkout master

rm -rf .tmp

