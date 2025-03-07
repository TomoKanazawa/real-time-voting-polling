import Link from 'next/link';

export default function Home() {
  return (
    <div className="bg-white shadow overflow-hidden sm:rounded-lg">
      <div className="px-4 py-5 sm:px-6">
        <h2 className="text-2xl font-bold text-gray-900">
          Welcome to the Distributed Real-Time Voting System
        </h2>
        <p className="mt-1 max-w-2xl text-sm text-gray-500">
          A scalable, fault-tolerant, real-time voting and polling system
        </p>
      </div>
      <div className="border-t border-gray-200 px-4 py-5 sm:p-6">
        <div className="prose max-w-none">
          <p>
            This system is designed to handle thousands of concurrent votes with minimal latency.
            It uses Apache Kafka for real-time messaging and implements leader election algorithms
            to ensure fault tolerance and consistency.
          </p>
          
          <h3 className="text-lg font-medium text-gray-900 mt-5">Key Features</h3>
          <ul className="mt-2 list-disc list-inside text-gray-600">
            <li>Real-time vote processing with instant results</li>
            <li>Fault tolerance with automatic recovery from node failures</li>
            <li>Scalability to handle thousands of concurrent users</li>
            <li>Leader election using distributed consensus algorithms</li>
            <li>Secure voting with protection against duplicate votes</li>
          </ul>
        </div>
        
        <div className="mt-8 flex flex-col sm:flex-row gap-4">
          <Link 
            href="/polls" 
            className="inline-flex items-center justify-center px-5 py-3 border border-transparent text-base font-medium rounded-md text-white bg-indigo-600 hover:bg-indigo-700"
          >
            View Active Polls
          </Link>
          <Link 
            href="/auth/login" 
            className="inline-flex items-center justify-center px-5 py-3 border border-gray-300 text-base font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            Login
          </Link>
          <Link 
            href="/auth/register" 
            className="inline-flex items-center justify-center px-5 py-3 border border-gray-300 text-base font-medium rounded-md text-gray-700 bg-white hover:bg-gray-50"
          >
            Register
          </Link>
        </div>
      </div>
    </div>
  );
}
