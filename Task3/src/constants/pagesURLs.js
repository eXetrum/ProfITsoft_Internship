import * as pages from './pages';
import config from 'config';

const result = {
  [pages.defaultPage]: `${config.UI_URL_PREFIX}/${pages.defaultPage}`,
  [pages.login]: `${config.UI_URL_PREFIX}/${pages.login}`,
  [pages.secretPage]: `${config.UI_URL_PREFIX}/${pages.secretPage}`,
  [pages.authorPage]: `${config.UI_URL_PREFIX}/${pages.authorPage}`,
  [pages.authorListPage]: `${config.UI_URL_PREFIX}/${pages.authorListPage}`,
  [pages.bookPage]: `${config.UI_URL_PREFIX}/${pages.bookPage}`,
  [pages.bookListPage]: `${config.UI_URL_PREFIX}/${pages.bookListPage}`,
};

export default result;
