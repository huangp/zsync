import React from 'react'
import { Route, IndexRoute } from 'react-router'

import Home from './containers/Home'
import App from './components/App'
import NewWork from './containers/NewWork'
import MyWorks from './containers/MyWorks'

const routes = (
  <Route path='/' component={App}>
    <IndexRoute component={Home} />
    <Route path='work/new' component={NewWork} />
    <Route path='work/mine' component={MyWorks} />
  </Route>
)

export default routes

