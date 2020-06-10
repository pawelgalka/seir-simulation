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
    
title=plt.figure()
txt = 'Disease Raport'
plt.axis('off')
plt.text(0.5,0.5,txt, transform=title.transFigure, size=36, ha="center")

fig1=plt.figure()
plt.title('Actually Infected')
plt.plot(covid['Infectious'])

fig2=plt.figure()
plt.title('Daily Dead')
clrs = ['C0' if (x < max(deaths)) else 'C3' for x in deaths ]
plt.bar(np.arange(len(deaths)),deaths, color=clrs)

fig3=plt.figure()
plt.title('Summarized Deaths')
plt.plot(covid['Dead'])

fig4=plt.figure()
plt.title('Daily Recovered')
clrs = ['C0' if (x < max(recov)) else 'C2' for x in recov ]
plt.bar(np.arange(len(recov)),recov, color=clrs)

fig5=plt.figure()
plt.title('Summarized Recoveries')
plt.plot(covid['Recovered'])

pp=PdfPages(csvPath[:-3]+"pdf")
pp.savefig(title)
pp.savefig(fig1)
pp.savefig(fig2)
pp.savefig(fig3)
pp.savefig(fig4)
pp.savefig(fig5)
pp.close()