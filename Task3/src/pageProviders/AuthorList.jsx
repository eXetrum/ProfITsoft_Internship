import AuthorListPage from 'pages/author-list';
import React from 'react';

import PageContainer from './components/PageContainer';

const AuthorList = (props) => {
  return (
    <PageContainer>
      <AuthorListPage {...props} />
    </PageContainer>
  );
};

export default AuthorList;
