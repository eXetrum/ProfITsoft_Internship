import AuthorDetailsPage from 'pages/author/details';
import React from 'react';

import PageContainer from './components/PageContainer';

const AuthorDetails = (props) => {
  return (
    <PageContainer>
      <AuthorDetailsPage {...props} />
    </PageContainer>
  );
};

export default AuthorDetails;
