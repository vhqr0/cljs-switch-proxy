css-watch:
	npx tailwindcss -o public/css/main.css -w

cljs-watch:
	npx shadow-cljs watch app portfolio

css-build:
	npx tailwindcss --optimize -o public/css/main.css

cljs-build:
	npx shadow-cljs release app

build: css-build cljs-build

.PHONY: dist
dist:
	mkdir -p dist/
	mkdir -p dist/img/
	mkdir -p dist/css/
	mkdir -p dist/js/app/
	cp public/manifest.json dist/manifest.json
	cp public/popup.html dist/popup.html
	cp public/img/favicon-16x16.png dist/img/favicon-16x16.png
	cp public/css/main.css dist/css/main.css
	cp public/js/app/main.js dist/js/app/main.js
