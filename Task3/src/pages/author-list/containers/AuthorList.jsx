import React, { useEffect } from 'react';
import { useIntl } from 'react-intl';
import { useDispatch, useSelector } from 'react-redux';
import actionsAuthor from '../actions/authors';

import Typography from 'components/Typography';

function AuthorList() {
  const { formatMessage } = useIntl();
  const dispatch = useDispatch();
  const { authors } = useSelector(state => state.authors);

  console.log('AuthorList authors: ', authors);

  useEffect(() => {
    console.log('AuthorList useEffect: ', authors);
    dispatch(actionsAuthor.fetchAuthors());
  }, []);


  return (
    <Typography>
      {formatMessage({ id: 'title' })}
    </Typography>
  );
}

export default AuthorList;
