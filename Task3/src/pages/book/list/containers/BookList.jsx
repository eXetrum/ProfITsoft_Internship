import React, { useEffect, useCallback, useState, useMemo } from 'react';
import { useNavigate } from 'react-router-dom';
import { createUseStyles } from 'react-jss';
import { useIntl } from 'react-intl';
import useTheme from 'misc/hooks/useTheme';
import useLocationSearch from 'misc/hooks/useLocationSearch';
import { useDispatch, useSelector } from 'react-redux';
import pageURLs from 'constants/pagesURLs';

import actionsBook from '../../actions/books';

import Typography from 'components/Typography';
import { DataGrid, GridActionsCellItem } from 'components/DataGrid';
import { AlertDialog } from 'components/AlertDialog';
import Button from 'components/Button';
import IconClose from 'components/icons/Close';

const getClasses = createUseStyles((theme) => ({
  buttons: {
    display: 'flex',
    gap: `${theme.spacing(1)}px`,
    justifyContent: 'center',
    margin: '10px 0 15px 0'
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
  },
  dialogContent: {
    display: 'flex',
    flexDirection: 'column',
    gap: `${theme.spacing(2)}px`,
  },
}));

function BookList() {
  const { formatMessage } = useIntl();
  const { lang } = useLocationSearch();
  const { theme } = useTheme();
  const classes = getClasses({ theme });
  const dispatch = useDispatch();  
  const navigate = useNavigate();

  const books = useSelector(state => state.books);
  const pageSize = useSelector(state => state.pageSize);
  const currentPage = useSelector(state => state.currentPage);
  const totalItems = useSelector(state => state.totalItems);
  const isLoading = useSelector(state => state.isLoading);

  const [paginationModel, setPaginationModel] = useState({
    page: currentPage,
    pageSize: pageSize,
  });

  const [deleteDialogueParams, setDeleteDialogueParams] = useState({
    open: false,
    bookId: -1,
    refetchRequired: false
  });

  const onBookDelete = useCallback( (id) => {
    setDeleteDialogueParams({ open: true, bookId: id, refetchRequired: false });
  }, [setDeleteDialogueParams]);

  const onAcceptRemove = useCallback( () => {
    dispatch(actionsBook.deleteBookById(deleteDialogueParams.bookId));
    setDeleteDialogueParams({ open: false, bookId: -1, refetchRequired: true });
    setTimeout(() => {
      dispatch(actionsBook.fetchBooksPage({ currentPage, pageSize }))
    }, 500);
  }, [currentPage, pageSize, deleteDialogueParams, setDeleteDialogueParams, dispatch]);

  const onCancelRemove = useCallback( () => {
    setDeleteDialogueParams({ open: false, bookId: -1, refetchRequired: false });
  }, [setDeleteDialogueParams])

  const onPaginationModelChange = useCallback(
    params => {
      if (params.page !== currentPage) {
        setPaginationModel(params);
        dispatch(actionsBook.changeBooksCurrentPage(params.page));
      }
      if (params.pageSize !== pageSize) {
        setPaginationModel(params);
        dispatch(actionsBook.changeBooksPageSize(params.pageSize))
      }
    },
    [currentPage, pageSize, dispatch],
  );

  const onRowClick = useCallback( 
    ({row, ...params}, event, details) => {
      navigate(`${pageURLs.bookDetails}/${row.id}?lang=${lang}`);
    },
    [lang, navigate]
  );

  const columns = useMemo( () => [
    { field: 'title', headerName: formatMessage({ id: 'column.title' }), flex: 1 },
    { field: 'genre', headerName: formatMessage({ id: 'column.genre' }), width: 240, flex: 1 },
    { field: 'publishYear', headerName: formatMessage({ id: 'column.publishYear' }), width: 240, flex: 1 },
    { field: 'authorName', headerName: formatMessage({ id: 'column.authorName' }), width: 240, flex: 1 },
    {
      field: "actions",
      type: "actions",
      width: 100,
      getActions: ({ row }) => [
        <GridActionsCellItem
          label="delete"
          icon={<IconClose />}
          onClick={() => onBookDelete(row.id) }
        />,
      ],
    },
  ], [formatMessage, onBookDelete]);

  useEffect(() => {
    dispatch(actionsBook.fetchBooksPage({ currentPage, pageSize }));
  }, [currentPage, pageSize, dispatch]);

  return (
    <>
      <Typography align='center' >
        {formatMessage({ id: 'title' })}
      </Typography>

      <div className={classes.buttons}>
          <Button
              isLoading={isLoading}
              onClick={() => navigate(`${pageURLs.bookDetails}?lang=${lang}`)}
              variant="primary"
            >
              <Typography color="inherit">
                <strong>
                  {formatMessage({ id: 'button.create' })}
                </strong>
              </Typography>
            </Button>
      </div>
      
      {!isLoading && 
      <DataGrid 
        className={classes.content}
        autoHeight
        disableRowSelectionOnClick
        disableColumnFilter
        loading={isLoading}        
        rows={books.map(({author, ...rest}) =>  ({...rest, authorName: author.name }) ) }
        columns={columns}
        paginationMode="server"
        sortingMode="server"
        filterMode="server"
        initialState={{
          pagination: { paginationModel }
        }}
        onPaginationModelChange={onPaginationModelChange}
        onRowClick={onRowClick}
        rowCount={totalItems}
        pageSizeOptions={[5, 10, 15]}
      />
      }
      <AlertDialog 
        isOpen={deleteDialogueParams.open} 
        title={''} 
        message={formatMessage({ id: 'confirm.delete' }) } 
        onAccept={onAcceptRemove}
        onClose={onCancelRemove}
      />
    </>
  );
}

export default BookList;
