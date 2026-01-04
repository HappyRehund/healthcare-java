import type { DoctorInfo } from "../types/api.types"
import { formatToIDR } from "../utils/currency.utils"

interface DoctorProps {
  doctor: DoctorInfo
}

const DoctorCard = ({doctor} : DoctorProps) => {
  return (
    <div className="bg-white rounded-lg shadow-md p-6 hover:shadow-lg transition-shadow">
      <div className="space-y-4">
        <div>
          <h3 className="text-xl font-semibold text-gray-900">{doctor.name}</h3>
          <p className="text-gray-600">{doctor.hospital_name}</p>
        </div>

        <p className="text-gray-700 text-sm">{doctor.bio}</p>

        <div className="space-y-2">
          <h4 className="font-medium text-gray-900">Specializations</h4>
          <div className="flex flex-wrap gap-2">
            {
              doctor.specializations.map((spec, index) => (
                <div key={index} className="bg-blue-500 text-blue-700 px-3 py-1 rounded-full text-sm">
                  {spec.specialization_name}
                </div>
              ))
            }
          </div>
        </div>

        <div className="space-y-2">
          <h4 className="font-medium text-gray-900">Consultation Types</h4>
            {
              doctor.specializations.map((spec, index) => (
                <div key={index} className="flex justify-between items-center text-sm">
                  <span className="text-gray-600">{spec.consultation_type}</span>
                  <span className="font-medium">{formatToIDR(spec.base_fee)}/hour</span>
                </div>
              ))
            }
        </div>

        <button
          className="w-full bg-indigo-600 text-white py-2 px-4 rounded-md hover:bg-indigo-700 transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-indigo-500"
          onClick={() => {}}
        >
          Book Appointment
        </button>
      </div>
    </div>
  )
}

export default DoctorCard;