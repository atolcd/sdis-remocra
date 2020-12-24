const path = require('path')

function resolve(dir) {
  return path.join(__dirname, '..', dir)
}

const devServerBaseUrl = process.env.DEV_SERVER_BASE_URL || 'http://0.0.0.0:8881'
const authUser = process.env.AUTH_USER || 'admin'

module.exports = {
  assetsDir: 'static',
  publicPath: '/',
  filenameHashing: true,
  outputDir: path.resolve(__dirname, 'dist'),
  devServer: {
    port: 8081,
    host: '0.0.0.0',
    proxy: {
      '/': {
        target: devServerBaseUrl,
        changeOrigin: true,
        'secure': false,
        ws: false,
        'headers': {
          'Auth-User': authUser
        }
      }
    }
  }
}
