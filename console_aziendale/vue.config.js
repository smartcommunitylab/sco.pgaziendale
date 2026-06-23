module.exports = {
  devServer: {
    host: 'localhost',
    proxy: {
      '/api': {
        target: 'https://pgaziendaledev.platform.smartcommunitylab.it',
        changeOrigin: true,
        secure: false,
      }
    }
  },
  transpileDependencies: [
    'vuetify'
  ]
}