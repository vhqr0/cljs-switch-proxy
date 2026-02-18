module.exports = {
  mode: "jit",
  purge: {
    content: ["./src/**/*.cljs", "./src/**/*.cljc"],
    options: {
      defaultExtractor: (content) => content.match(/[^<>"'.`\s]*[^<>"'.`\s:]/g),
    },
  },
};
