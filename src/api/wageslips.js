import api from './axios'

export const generateWageSlip = (workerId, year, month) =>
  api.post(
    `/wage-slips/generate/${workerId}/${year}/${month}`)

export const generateAllWageSlips = (siteId, year, month) =>
  api.post(
    `/wage-slips/generate-all/${siteId}/${year}/${month}`)

export const downloadWageSlip = (slipId) =>
  api.get(`/wage-slips/pdf/${slipId}`,
    { responseType: 'blob' })

export const getWageSlipsBySite = (siteId, year, month) =>
  api.get(`/wage-slips/site/${siteId}/${year}/${month}`)