import api from './axios'

export const registerWorker = (data) =>
  api.post('/workers/register', data)

export const getWorkersBySite = (siteId) =>
  api.get(`/workers/site/${siteId}`)

export const getWorkerById = (id) =>
  api.get(`/workers/${id}`)

export const getWorkerQR = (id) =>
  api.get(`/workers/${id}/qr`, { responseType: 'blob' })