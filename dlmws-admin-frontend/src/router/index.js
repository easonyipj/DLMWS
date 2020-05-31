import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router)

/* Layout */
import Layout from '@/layout'

/**
 * Note: sub-menu only appear when route children.length >= 1
 * Detail see: https://panjiachen.github.io/vue-element-admin-site/guide/essentials/router-and-nav.html
 *
 * hidden: true                   if set true, item will not show in the sidebar(default is false)
 * alwaysShow: true               if set true, will always show the root menu
 *                                if not set alwaysShow, when item has more than one children route,
 *                                it will becomes nested mode, otherwise not show the root menu
 * redirect: noRedirect           if set noRedirect will no redirect in the breadcrumb
 * name:'router-name'             the name is used by <keep-alive> (must set!!!)
 * meta : {
    roles: ['admin','editor']    control the page roles (you can set multiple roles)
    title: 'title'               the name show in sidebar and breadcrumb (recommend set)
    icon: 'svg-name'             the icon show in the sidebar
    breadcrumb: false            if set false, the item will hidden in breadcrumb(default is true)
    activeMenu: '/example/list'  if set path, the sidebar will highlight the path you set
  }
 */

/**
 * constantRoutes
 * a base page that does not have permission requirements
 * all roles can be accessed
 */
export const constantRoutes = [
  {
    path: '/login',
    component: () => import('@/views/login/index'),
    hidden: true
  },

  {
    path: '/404',
    component: () => import('@/views/404'),
    hidden: true
  },

  {
    path: '/',
    component: Layout,
    redirect: '/dashboard',
    children: [{
      path: 'dashboard',
      name: '数据大盘',
      component: () => import('@/views/dashboard/index'),
      meta: { title: '数据大盘', icon: 'dashboard' }
    }]
  },
  {
    path: '/deploy',
    component: Layout,
    redirect: '/deploy',
    children: [{
      path: 'deploy',
      name: '脚本部署',
      component: () => import('@/views/deploy/index'),
      meta: { title: '脚本部署', icon: 'password' }
    }]
  },
  {
    path: '/monitor',
    component: Layout,
    redirect: '/monitor/service',
    name: '监控中心',
    meta: { title: '监控中心', icon: 'example' },
    children: [
      {
        path: 'host',
        name: '主机监控',
        component: () => import('@/views/host/index'),
        meta: { title: '主机监控', icon: 'tree' }
      },
      {
        path: 'service',
        name: '服务日志',
        component: () => import('@/views/monitor/index'),
        meta: { title: '服务日志', icon: 'table' }
      },
      {
        path: 'jvm',
        name: 'JVM监控',
        component: () => import('@/views/jvm/index'),
        meta: { title: 'JVM监控', icon: 'nested' }
      }
    ]
  },
  {
    path: '/warn',
    component: Layout,
    redirect: '/warn/list',
    name: '报警中心',
    meta: { title: '报警中心', icon: 'form' },
    children: [
      {
        path: 'list',
        name: '报警列表',
        component: () => import('@/views/warn/index'),
        meta: { title: '报警列表', icon: 'nested' }
      },
      {
        path: 'statistic',
        name: '报警统计',
        component: () => import('@/views/warn/statistic'),
        meta: { title: '报警统计', icon: 'form' }
      },
      {
        path: 'rule',
        name: '规则配置',
        component: () => import('@/views/rule/index'),
        meta: { title: '规则配置', icon: 'link' }
      },
    ]
  },
  {
    path: '/stack',
    component: Layout,
    redirect: '/stack/kibana',
    name: '组件监控',
    meta: { title: '组件监控', icon: 'eye' },
    children: [
      {
        path: 'kibana',
        name: 'Kibana',
        component: () => import('@/views/stack/kibana'),
        meta: { title: 'Kibana', icon: 'edit' }
      },
      {
        path: 'druid',
        name: 'Druid',
        url: 'http://127.0.0.1:8088/druid/sql.html',
        // component: () => import('@/views/stack/statistic'),
        meta: { title: 'Druid', icon: 'email' }
      },
      {
        path: 'storm',
        name: 'Storm',
        component: () => import('@/views/rule/index'),
        meta: { title: 'Storm', icon: 'excel' }
      },
    ]
  },
  { path: '*', redirect: '/404', hidden: true }
]

const createRouter = () => new Router({
  // mode: 'history', // require service support
  scrollBehavior: () => ({ y: 0 }),
  routes: constantRoutes
})

const router = createRouter()

// Detail see: https://github.com/vuejs/vue-router/issues/1234#issuecomment-357941465
export function resetRouter() {
  const newRouter = createRouter()
  router.matcher = newRouter.matcher // reset router
}

export default router
