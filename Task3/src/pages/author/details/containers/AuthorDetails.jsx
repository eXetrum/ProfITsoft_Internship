import { createUseStyles } from 'react-jss';
import { useIntl } from 'react-intl';
import useTheme from 'misc/hooks/useTheme';
import useLocationSearch from 'misc/hooks/useLocationSearch';
import React, { useEffect, useMemo, useCallback } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import actionsAuthor from '../../actions/authors';
import pageURLs from 'constants/pagesURLs';
import TextField from 'components/TextField';
import Typography from 'components/Typography';
import Button from 'components/Button';

const getClasses = createUseStyles((theme) => ({
  buttons: {
    display: 'flex',
    gap: `${theme.spacing(1)}px`,
    justifyContent: 'center',
  },
  container: {
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
  },
  content: {
    display: 'flex',
    flexDirection: 'column',
    gap: `${theme.spacing(2)}px`,
    width: '300px',
  },
  dialogContent: {
    display: 'flex',
    flexDirection: 'column',
    gap: `${theme.spacing(2)}px`,
  },
}));

function AuthorDetails() {
  const { formatMessage } = useIntl();
  const { lang } = useLocationSearch();
  const { theme } = useTheme();
  const classes = getClasses({ theme });  
  const { authorId } = useParams();
  const dispatch = useDispatch(); 
  const navigate = useNavigate();
  
  const author = useSelector(state => state.author);
  const isLoading = useSelector(state => state.isLoading);
  const isSuccess = useSelector(state => state.isSuccess);
  const isError = useSelector(state => state.isError);
  const error = useSelector(state => state.error);
  
  const isCreateMode = useMemo(() => authorId === undefined, [authorId]);
  const isEditMode = useMemo(() => !isCreateMode, [isCreateMode]);

  const onFieldChange = useCallback( (fieldName) => 
    ({ target }) => {
      dispatch(actionsAuthor.authorFieldChange( { [fieldName]: target.value }))
    },
    [dispatch]
  );

  useEffect( () => {
    let timeoutId = null;
    if(isSuccess && isCreateMode) {
      dispatch(actionsAuthor.resetAuthor());
      timeoutId = setTimeout(()=>{
        dispatch(actionsAuthor.resetSuccess());
       }, 4000)
    }
    return () => {
      if(timeoutId !== null)
        clearTimeout(timeoutId);
    }
  }, [isSuccess, isCreateMode, dispatch]);

  useEffect( () => {
    if(isEditMode) {
      dispatch(actionsAuthor.fetchAuthorById(authorId));
    }
  }, [isEditMode, authorId, dispatch]);

  return (
    <div className={classes.container}>
      <div className={classes.content}>
        <Typography>
          {formatMessage({ id: `${isCreateMode ? 'create' : 'edit'}.title` })}
        </Typography>

        <TextField
          label={formatMessage({ id: 'field.name' })}
          onChange={onFieldChange('name')}
          value={author.name}
        />
        <TextField
          inputType='number'
          label={formatMessage({ id: 'field.birthdayYear' })}
          onChange={onFieldChange('birthdayYear')}
          value={author.birthdayYear}
        />
        
        {isError && (
          <Typography color="error">
            { formatMessage({ id: 'op.error' }) }: {error}
          </Typography>
        )}

        {isSuccess && (
          <Typography color="success">
            { formatMessage({ id: `op.success.${isCreateMode ? 'create': 'save'}` }) }
          </Typography>
        )}

        <div className={classes.buttons}>
          <Button
              disabled={!author.name || !author.birthdayYear}
              isLoading={isLoading}
              onClick={() => {
                if(isCreateMode)
                  dispatch(actionsAuthor.createAuthor({...author}));
                else
                  dispatch(actionsAuthor.updateAuthorById({ id: authorId, author }));
              }}
              variant="primary"
            >
              <Typography color="inherit">
                <strong>
                  {formatMessage({ id: `button.${isCreateMode ? 'create' : 'save' }` })}
                </strong>
              </Typography>
            </Button>

            <Button
              isLoading={isLoading}
              onClick={() => {
                navigate(`${pageURLs.authorList}?lang=${lang}`)
              }}
              variant="default"
            >
              <Typography color="inherit">
                <strong>
                  {formatMessage({ id: 'button.cancel' })}
                </strong>
              </Typography>
            </Button>
        </div>
      </div>
    </div>
  );
}

export default AuthorDetails;
