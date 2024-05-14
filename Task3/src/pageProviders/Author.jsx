import AuthorPage from 'pages/author';
import React from 'react';

import PageContainer from './components/PageContainer';

const Author = (props) => {
  return (
    <PageContainer>
      <AuthorPage {...props} />
    </PageContainer>
  );
};

export default Author;
