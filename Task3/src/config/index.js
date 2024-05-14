const config = {
  // Services
  USERS_SERVICE: 'http://localhost:3000',
  BOOKS_LIBRARY_SERVICE: 'http://localhost:8080/api',
  UI_URL_PREFIX: process.env.REACT_APP_UI_URL_PREFIX || '',
};

export default config;
