import {QueryClient, QueryClientProvider} from 'react-query';
import { ReactQueryDevtools } from 'react-query/devtools'
import React, { useState, useEffect  } from "react";
import MovieComponent from './MovieComponent';
import { ChakraProvider } from '@chakra-ui/react'

const queryClient = new QueryClient({});

const App = () => {
  
  return (
    <ChakraProvider>
      <QueryClientProvider client={queryClient}> 
        <MovieComponent/>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  </ChakraProvider>
  );

};

export default App;
