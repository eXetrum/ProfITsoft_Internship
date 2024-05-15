import { createUseStyles } from 'react-jss';
import { useIntl } from 'react-intl';
import useTheme from 'misc/hooks/useTheme';
import useLocationSearch from 'misc/hooks/useLocationSearch';
import React, { useEffect, useMemo, useCallback, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useDispatch, useSelector } from 'react-redux';
import actionsBook from '../../actions/books';
import pageURLs from 'constants/pagesURLs';
import TextField from 'components/TextField';
import Typography from 'components/Typography';
import Button from 'components/Button';
import DropDown from 'components/DropDown/DropDown';

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

function BookDetails() {
  const { formatMessage } = useIntl();
  const { lang } = useLocationSearch();
  const { theme } = useTheme();
  const classes = getClasses({ theme });  
  const { bookId } = useParams();
  const dispatch = useDispatch(); 
  const navigate = useNavigate();
  
  const book = useSelector(state => state.book);
  const authors = [
    {id: '429833b5-5b74-4e47-b9b4-49cab29d384d', name: 'test'}, 
    {id: '983c59fd-f773-42c1-a1a1-0f23c839a232', name: 'test2'}
  ];//useSelector(state => state.authors);
  
  const isLoading = useSelector(state => state.isLoading);
  const isSuccess = useSelector(state => state.isSuccess);
  const isError = useSelector(state => state.isError);
  const error = useSelector(state => state.error);

  const [selectedAuthor, setSelectedAuthor] = useState(authors[0]);
  
  const isCreateMode = useMemo(() => bookId === undefined, [bookId]);
  const isEditMode = useMemo(() => !isCreateMode, [isCreateMode]);

  const onFieldChange = useCallback( (fieldName) => 
    ({ target }) => {
      dispatch(actionsBook.bookFieldChange( { [fieldName]: target.value }))
    },
    [dispatch]
  );

  useEffect( () => {
    let timeoutId = null;
    if(isSuccess && isCreateMode) {
      dispatch(actionsBook.resetBook());
      timeoutId = setTimeout(()=>{
        dispatch(actionsBook.resetSuccess());
       }, 4000)
    }
    return () => {
      if(timeoutId !== null)
        clearTimeout(timeoutId);
    }
  }, [isSuccess, isCreateMode, dispatch]);

  useEffect( () => {
    if(isEditMode) {
      dispatch(actionsBook.fetchBookById(bookId));
    }
  }, [isEditMode, bookId, dispatch]);

  return (
    <div className={classes.container}>
      <div className={classes.content}>
        <Typography>
          {formatMessage({ id: `${isCreateMode ? 'create' : 'edit'}.title` })}
        </Typography>

        <TextField
          label={formatMessage({ id: 'field.title' })}
          onChange={onFieldChange('title')}
          value={book.title}
        />
        <TextField
          label={formatMessage({ id: 'field.genre' })}
          onChange={onFieldChange('genre')}
          value={book.genre}
        />
        <TextField
          inputType='number'
          label={formatMessage({ id: 'field.publishYear' })}
          onChange={onFieldChange('publishYear')}
          value={book.publishYear}
        />
        <DropDown 
          items={authors}
          value={selectedAuthor.id}
          label={ formatMessage({ id: 'field.author' }) }
          title={ formatMessage({ id: 'field.author' }) }
          onChange={(event) => {
            const id = event.target.value;
            const author = authors.find(item => item.id === id);
            if(author) {
              setSelectedAuthor(author);
              onFieldChange('authorId')(event);
            }
          }}
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
              disabled={!book.title || !book.genre || !book.publishYear || !book.authorId}
              isLoading={isLoading}
              onClick={() => {
                if(isCreateMode)
                  dispatch(actionsBook.createBook({...book}));
                else
                  dispatch(actionsBook.updateBookById({ id: bookId, book }));
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
                navigate(`${pageURLs.bookList}?lang=${lang}`)
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

export default BookDetails;
