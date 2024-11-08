const path = require('path')

function resolve(dir) {
  return path.join(__dirname, '..', dir)
}
module.exports = {
  assetsDir: 'remocra/static',
  publicPath: '/',
  filenameHashing: false,
  outputDir: path.resolve(__dirname, 'dist'),
  devServer: {
    port: 8081,
    host: '0.0.0.0',
    proxy: {
      '/remocra': {
        target: 'http://0.0.0.0:8080',
        changeOrigin: true
      }
    }
  },
  // exporter l'appli en tant que librairie
  configureWebpack: {
    output: {
      library: 'remocraVue',
      libraryTarget: 'var'
    },
    resolve: {
      extensions: ['.js', '.vue', '.json'],
      alias: {
        'vue$': 'vue/dist/vue.esm.js',
        '@': resolve('src'),
      }
    }
  }
}
