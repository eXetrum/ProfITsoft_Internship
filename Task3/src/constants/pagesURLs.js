import * as pages from './pages';
import config from 'config';

const result = {
  [pages.defaultPage]: `${config.UI_URL_PREFIX}/${pages.defaultPage}`,
  [pages.login]: `${config.UI_URL_PREFIX}/${pages.login}`,
  [pages.secretPage]: `${config.UI_URL_PREFIX}/${pages.secretPage}`,
  [pages.authorDetailsPage]: `${config.UI_URL_PREFIX}/${pages.authorDetailsPage}`,
  [pages.authorListPage]: `${config.UI_URL_PREFIX}/${pages.authorListPage}`,
  [pages.bookDetailsPage]: `${config.UI_URL_PREFIX}/${pages.bookDetailsPage}`,
  [pages.bookListPage]: `${config.UI_URL_PREFIX}/${pages.bookListPage}`,
};

export default result;
