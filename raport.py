import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
import sys
from matplotlib.backends.backend_pdf import PdfPages

csvPath=sys.argv[1]

covid=pd.read_csv(csvPath)
covidEdit=covid.copy()

deaths=covidEdit['Dead'].values
for i in range(deaths.size-1,0,-1):
    deaths[i]=deaths[i]-deaths[i-1]

recov=covidEdit['Recovered'].values
for i in range(recov.size-1,0,-1):
    recov[i]=recov[i]-recov[i-1]
    
    
fig1=plt.figure()
plt.title('Actually Infected')
txt = 'Disease Raport'
plt.text(0.05,0.95,txt, transform=fig1.transFigure, size=36)
plt.plot(covid['Infectious'])

fig2=plt.figure()
plt.title('Daily Dead')
plt.bar(np.arange(len(deaths)),deaths)

fig3=plt.figure()
plt.title('Actually Dead')
plt.plot(covid['Dead'])

fig4=plt.figure()
plt.title('Daily Recovered')
plt.bar(np.arange(len(recov)),recov)

fig5=plt.figure()
plt.title('Actually Recovered')
plt.plot(covid['Recovered'])

pp=PdfPages(csvPath[:-3]+"pdf")
pp.savefig(fig1)
pp.savefig(fig2)
pp.savefig(fig3)
pp.savefig(fig4)
pp.savefig(fig5)
pp.close()