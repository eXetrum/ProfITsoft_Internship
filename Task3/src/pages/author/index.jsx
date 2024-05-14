import React, { useMemo } from 'react';
import IntlProvider from 'misc/providers/IntlProvider';
import useLocationSearch from 'misc/hooks/useLocationSearch';

import getMessages from './intl';
import Author from './containers/Author';

function Index(props) {
  const {
    lang,
  } = useLocationSearch();
  const messages = useMemo(() => getMessages(lang), [lang]);
  return (
    <IntlProvider messages={messages}>
      <Author {...props} />
    </IntlProvider>
  );
}

export default Index;
